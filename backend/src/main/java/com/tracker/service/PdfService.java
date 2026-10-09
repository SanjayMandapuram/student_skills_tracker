package com.tracker.service;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Small dependency-free PDF writer for text reports. It emits real PDF bytes and paginates long reports. */
@Service
public class PdfService {
  public byte[] textDocument(String title, List<String> sourceLines) {
    List<String> lines = new ArrayList<>();
    lines.add(title); lines.add("");
    sourceLines.forEach(line -> wrap(lines, line == null ? "" : line, 96));
    int pageCount = Math.max(1, (lines.size() + 44) / 45);
    List<String> objects = new ArrayList<>();
    objects.add("<< /Type /Catalog /Pages 2 0 R >>");
    StringBuilder kids = new StringBuilder("[");
    for (int i = 0; i < pageCount; i++) kids.append(3 + i * 3).append(" 0 R ");
    kids.append("]");
    objects.add("<< /Type /Pages /Kids " + kids + " /Count " + pageCount + " >>");
    for (int page = 0; page < pageCount; page++) {
      int from = page * 45, to = Math.min(lines.size(), from + 45);
      String stream = stream(lines.subList(from, to));
      int contentObject = 4 + page * 3;
      objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 " + (5 + page * 3) + " 0 R >> >> /Contents " + contentObject + " 0 R >>");
      objects.add("<< /Length " + stream.getBytes(StandardCharsets.ISO_8859_1).length + " >>\nstream\n" + stream + "\nendstream");
      objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
    }
    return serialize(objects);
  }

  private void wrap(List<String> out, String text, int width) {
    if (text.isEmpty()) { out.add(""); return; }
    String remaining = text.replaceAll("[\\r\\n]+", " ").trim();
    while (remaining.length() > width) { int cut = remaining.lastIndexOf(' ', width); if (cut < 1) cut = width; out.add(remaining.substring(0, cut)); remaining = remaining.substring(cut).trim(); }
    out.add(remaining);
  }

  private String stream(List<String> lines) {
    StringBuilder b = new StringBuilder("BT\n/F1 10 Tf\n50 750 Td\n");
    for (String line : lines) { b.append('(').append(escape(line)).append(") Tj\n0 -16 Td\n"); }
    return b.append("ET").toString();
  }
  private String escape(String value) { return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replaceAll("[^\\x20-\\x7E]", "?"); }
  private byte[] serialize(List<String> objects) {
    StringBuilder pdf = new StringBuilder("%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");
    List<Integer> offsets = new ArrayList<>(); offsets.add(0);
    for (int i = 0; i < objects.size(); i++) { offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length); pdf.append(i + 1).append(" 0 obj\n").append(objects.get(i)).append("\nendobj\n"); }
    int xref = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
    pdf.append("xref\n0 ").append(objects.size() + 1).append("\n0000000000 65535 f \n");
    for (int i = 1; i < offsets.size(); i++) pdf.append(String.format("%010d 00000 n \n", offsets.get(i)));
    pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
    return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
  }
}
