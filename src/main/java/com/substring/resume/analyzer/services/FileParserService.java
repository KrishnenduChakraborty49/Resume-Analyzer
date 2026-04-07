package com.substring.resume.analyzer.services;

import com.substring.resume.analyzer.Exception.DocumentParseException;
import lombok.extern.slf4j.Slf4j;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.converter.text.StringTextConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class FileParserService {

    // Supported MIME types and extensions
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        "pdf", "docx", "doc", "odt", "rtf", "txt", "html", "htm"
    );

    private static final Map<String, String> MIME_EXTENSION_MAP = Map.of(
        "application/pdf",                                                  "pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx",
        "application/msword",                                               "doc",
        "application/vnd.oasis.opendocument.text",                          "odt",
        "application/rtf",                                                  "rtf",
        "text/rtf",                                                         "rtf",
        "text/plain",                                                       "txt",
        "text/html",                                                        "html"
    );

    /**
     * Main entry point — detects format and extracts clean text.
     * Supports: PDF, DOCX, DOC, ODT, RTF, TXT, HTML, HTM
     * Falls back to Apache Tika for any unrecognized format.
     */
    public String extractText(MultipartFile file) throws IOException {
        validateFile(file);

        String extension = getExtension(file);
        String extractedText;

        log.info("Extracting text from file: {} (extension: {})", file.getOriginalFilename(), extension);

        try {
            extractedText = switch (extension) {
                case "pdf"  -> extractFromPdf(file);
                case "docx" -> extractFromDocx(file);
                case "doc"  -> extractFromDoc(file);
                case "odt"  -> extractFromOdt(file);
                case "rtf"  -> extractFromRtf(file);
                case "txt"  -> extractFromTxt(file);
                case "html", "htm" -> extractFromHtml(file);
                default     -> extractWithTika(file);  // universal fallback
            };
        } catch (Exception e) {
            log.warn("Primary parser failed for {}, falling back to Tika: {}", extension, e.getMessage());
            extractedText = extractWithTika(file);
        }

        if (extractedText == null || extractedText.isBlank()) {
            throw new DocumentParseException(
                "Could not extract any text from the uploaded file. " +
                "If this is a scanned document, please upload a text-based version."
            );
        }

        String cleaned = cleanText(extractedText);
        log.info("Extracted {} characters from {}", cleaned.length(), file.getOriginalFilename());
        return cleaned;
    }

    // ── PDF ──────────────────────────────────────────────────────────────
    private String extractFromPdf(MultipartFile file) throws IOException {
        // Updated for PDFBox 2.x compatibility
        try (PDDocument doc = PDDocument.load(file.getInputStream())) {
            if (doc.isEncrypted()) {
                throw new DocumentParseException("The PDF is password-protected. Please upload an unlocked PDF.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);   // preserves reading order
            String text = stripper.getText(doc);

            if (text.isBlank()) {
                throw new DocumentParseException(
                        "This PDF appears to be scanned or image-based. " +
                                "Please upload a text-based PDF or convert it using an OCR tool first."
                );
            }
            return text;
        }
    }


    // ── DOCX ─────────────────────────────────────────────────────────────
    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            String text = extractor.getText();

            // Also extract text from tables inside DOCX
            StringBuilder tableText = new StringBuilder();
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        tableText.append(cell.getText()).append(" | ");
                    }
                    tableText.append("\n");
                }
            }
            return text + "\n" + tableText;
        }
    }

    // ── DOC (legacy Word 97-2003) ─────────────────────────────────────────
    private String extractFromDoc(MultipartFile file) throws IOException {
        try (HWPFDocument doc = new HWPFDocument(file.getInputStream())) {
            WordExtractor extractor = new WordExtractor(doc);
            return extractor.getText();
        }
    }

    // ── ODT (LibreOffice Writer) ──────────────────────────────────────────
    private String extractFromOdt(MultipartFile file) throws IOException {
        try {
            OdfTextDocument odt = OdfTextDocument.loadDocument(file.getInputStream());
            StringBuilder sb = new StringBuilder();
            NodeList paragraphs = odt.getContentDom()
                .getElementsByTagNameNS(OdfTextParagraph.ELEMENT_NAME.getUri(), "p");
            for (int i = 0; i < paragraphs.getLength(); i++) {
                sb.append(paragraphs.item(i).getTextContent()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            // Tika handles ODT well as fallback
            throw new IOException("ODT parse failed: " + e.getMessage(), e);
        }
    }

    // ── RTF ───────────────────────────────────────────────────────────────
    private String extractFromRtf(MultipartFile file) throws IOException {
        StringTextConverter converter = new StringTextConverter();
        converter.convert(new RtfStreamSource(file.getInputStream()));
        return converter.getText();
    }

    // ── TXT ───────────────────────────────────────────────────────────────
    private String extractFromTxt(MultipartFile file) throws IOException {
        // Detect encoding (UTF-8 first, fallback to system default)
        byte[] bytes = file.getBytes();
        String text;
        try {
            text = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            text = new String(bytes, Charset.defaultCharset());
        }
        return text;
    }

    // ── HTML / HTM ────────────────────────────────────────────────────────
    private String extractFromHtml(MultipartFile file) throws IOException {
        String html = new String(file.getBytes(), StandardCharsets.UTF_8);
        // Use Jsoup to strip tags and extract readable text
        Document jsoupDoc = Jsoup.parse(html);
        jsoupDoc.select("script, style, nav, footer, header").remove();
        return jsoupDoc.body().text();
    }

    // ── APACHE TIKA (Universal Fallback) ──────────────────────────────────
    // Tika auto-detects format and extracts text from almost any document type
    private String extractWithTika(MultipartFile file) throws IOException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(10 * 1024 * 1024); // 10MB text limit
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());
        ParseContext context = new ParseContext();
        try {
            parser.parse(file.getInputStream(), handler, metadata, context);
            return handler.toString();
        } catch (TikaException | SAXException e) {
            throw new IOException("Tika could not parse this file: " + e.getMessage(), e);
        }
    }

    // ── VALIDATION ────────────────────────────────────────────────────────
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DocumentParseException("No file uploaded or file is empty.");
        }

        long maxSizeBytes = 15 * 1024 * 1024; // 15 MB
        if (file.getSize() > maxSizeBytes) {
            throw new DocumentParseException("File is too large. Maximum allowed size is 15 MB.");
        }

        String extension = getExtension(file);
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new DocumentParseException(
                "Unsupported file format: ." + extension + ". " +
                "Supported formats: PDF, DOCX, DOC, ODT, RTF, TXT, HTML"
            );
        }
    }

    private String getExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            // Try to detect from MIME type
            String mime = file.getContentType();
            if (mime != null && MIME_EXTENSION_MAP.containsKey(mime)) {
                return MIME_EXTENSION_MAP.get(mime);
            }
            throw new DocumentParseException("Cannot determine file type. Please ensure the file has a proper extension.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase().trim();
    }

    // ── TEXT CLEANING ─────────────────────────────────────────────────────
    // Normalizes extracted text for cleaner LLM input
    private String cleanText(String raw) {
        return raw
            .replaceAll("\r\n", "\n")           // normalize line endings
            .replaceAll("\r", "\n")
            .replaceAll("[ \t]+", " ")           // collapse horizontal whitespace
            .replaceAll("\n{3,}", "\n\n")        // max 2 consecutive blank lines
            .replaceAll("(?m)^[ \t]+", "")       // remove leading whitespace per line
            .replaceAll("[^\\x20-\\x7E\n]", " ")  // remove non-printable characters
            .trim();
    }
}
