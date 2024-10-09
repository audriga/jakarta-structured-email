package com.audriga.jakarta.sml.test.scanner;

import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.model.StructuredSyntax;
import com.audriga.jakarta.sml.parser.StructuredMimeParseUtils;
import com.audriga.jakarta.sml.test.TestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class ParserTest {

    @BeforeClass
    public void setUp() {
        TestUtils.initLogging();
    }

    @Test
    public void testGetJsonLd() {
        // Read from jsonld-inline-body.html file
        String html = TestUtils.readResource("html-body/jsonld-inline-body.html");

        List<StructuredData> data = StructuredMimeParseUtils.parseStructuredDataPart(html, StructuredSyntax.JSON_LD);
        assertFalse(data.isEmpty());
    }

    @Test
    public void testGetMicrodata() {
        String html = TestUtils.readResource("microdata/flight-reservation-lufthansa.html");

        List<StructuredData> data = StructuredMimeParseUtils.parseStructuredDataPart(html, StructuredSyntax.MICRODATA);
        assertFalse(data.isEmpty());
        assertEquals(data.size(), 1);
    }

    @Test
    public void testGetMicrodataWithSpan() {
        String html = TestUtils.readResource("microdata/creative-work-docusign.html");

        List<StructuredData> data = StructuredMimeParseUtils.parseStructuredDataPart(html, StructuredSyntax.MICRODATA);
        assertFalse(data.isEmpty());
        assertEquals(data.size(), 1);
        StructuredData jsonData = data.get(0);
        assertTrue(jsonData.getJson().getJSONObject("about").getString("@type").contains("CreativeWork"));
    }

    @Test
    public void testGetMicrodataWithTime() {
        String html = TestUtils.readResource("microdata/lodging-reservation-hrs.html");

        List<StructuredData> data = StructuredMimeParseUtils.parseStructuredDataPart(html, StructuredSyntax.MICRODATA);
        assertFalse(data.isEmpty());
        assertEquals(data.size(), 1);
        StructuredData jsonData = data.get(0);
        assertEquals(jsonData.getJson().getString("checkinDate"), "2019-02-04T00:00:00");
    }
}