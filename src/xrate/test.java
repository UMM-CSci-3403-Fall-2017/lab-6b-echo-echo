package xrate;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class test {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		ExchangeRateReader reader = new ExchangeRateReader("http://api.finance.xaviermedia.com/api/2010/09/09.xml");

	}

}
