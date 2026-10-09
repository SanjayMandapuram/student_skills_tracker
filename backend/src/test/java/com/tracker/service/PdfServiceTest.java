package com.tracker.service;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {
  @Test void generatesRealPdfAndPaginatesLongContent() {
    PdfService service = new PdfService();
    byte[] pdf = service.textDocument("Achievement Report", java.util.stream.IntStream.range(0, 100).mapToObj(i -> "Line " + i).toList());
    String header = new String(pdf, 0, 8, StandardCharsets.ISO_8859_1);
    assertTrue(header.startsWith("%PDF-1.4"));
    assertTrue(new String(pdf, StandardCharsets.ISO_8859_1).contains("Line 99"));
    assertTrue(new String(pdf, StandardCharsets.ISO_8859_1).contains("/Count 3"));
  }
}
