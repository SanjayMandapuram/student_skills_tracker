package com.tracker.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class EvidenceStorageServiceTest {
  @Test void storesPdfWithSafeKeyAndReadsItBack() throws Exception {
    var directory = Files.createTempDirectory("evidence-test");
    var service = new EvidenceStorageService(directory.toString());
    var file = new MockMultipartFile("file", "../../certificate.pdf", "application/pdf", "%PDF-test".getBytes());
    var stored = service.store(file);
    assertTrue(stored.key().matches("^[0-9a-fA-F-]{36}$"));
    assertArrayEquals("%PDF-test".getBytes(), service.read(stored.key()));
    assertFalse(stored.name().contains("/"));
  }

  @Test void rejectsInvalidPdfMagicBytes() throws Exception {
    var service = new EvidenceStorageService(Files.createTempDirectory("evidence-invalid").toString());
    var file = new MockMultipartFile("file", "fake.pdf", "application/pdf", "not a pdf".getBytes());
    assertThrows(RuntimeException.class, () -> service.store(file));
  }
}
