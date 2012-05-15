package org.cloudsmith.jenkins.stackhammer.common;

import hudson.Functions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class GraphTrimmer {
	private static Templates STRIP_FIXED_SIZE_TEMPLATE;

	static {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			URL resourcePath = GraphTrimmer.class.getResource("zoom.xsl");
			File file = new File(resourcePath.toURI());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				STRIP_FIXED_SIZE_TEMPLATE = tFactory.newTemplates(new StreamSource(reader));
			}
			finally {
				reader.close();
			}
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static byte[] stripFixedSize(byte[] bytes) {
		try {
			Transformer transformer = STRIP_FIXED_SIZE_TEMPLATE.newTransformer();
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXSource saxSource = new SAXSource(reader, new InputSource(new ByteArrayInputStream(bytes)));
			transformer.setParameter("stackhammer", Functions.getResourcePath() + "/plugin/stackhammer");
			transformer.transform(saxSource, new StreamResult(result));
			return result.toByteArray();
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
