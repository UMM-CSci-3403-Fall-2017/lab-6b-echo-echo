package xrate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author PUT YOUR TEAM NAME HERE
 */
public class ExchangeRateReader {
	private String urlString;
	private URL url;
	private InputStream xmlStream;

	/**
	 * Construct an exchange rate reader using the given base URL. All requests
	 * will then be relative to that URL. If, for example, your source is Xavier
	 * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
	 * for specific days will be constructed from that URL by appending the
	 * year, month, and day; the URL for 25 June 2010, for example, would be
	 * http://api.finance.xaviermedia.com/api/2010/06/25.xml
	 * 
	 * @param baseURL
	 *            the base URL for requests
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public ExchangeRateReader(String baseURL) throws IOException, ParserConfigurationException, SAXException {
		urlString = baseURL;
	}

	/**
	 * Get the exchange rate for the specified currency against the base
	 * currency (the Euro) on the specified date.
	 * 
	 * @param currencyCode
	 *            the currency code for the desired currency
	 * @param year
	 *            the year as a four digit integer
	 * @param month
	 *            the month as an integer (1=Jan, 12=Dec)
	 * @param day
	 *            the day of the month as an integer
	 * @return the desired exchange rate
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public float getExchangeRate(String currencyCode, int year, int month, int day)
			throws IOException, ParserConfigurationException, SAXException {
		
		String m = month + "";
		String d = day + "";
		
		if(m.length() < 2){
			m = 0 + m;
		}
		
		if(d.length() < 2){
			d = 0 + d;
		}
		
		urlString += year + "/" + m + "/" + d + ".xml";
		url = new URL(urlString);
		xmlStream = url.openStream();
		XMLReader reader = new XMLReader();
		return reader.findRate(currencyCode, xmlStream);
	}

	/**
	 * Get the exchange rate of the first specified currency against the second
	 * on the specified date.
	 * 
	 * @param currencyCode
	 *            the currency code for the desired currency
	 * @param year
	 *            the year as a four digit integer
	 * @param month
	 *            the month as an integer (1=Jan, 12=Dec)
	 * @param day
	 *            the day of the month as an integer
	 * @return the desired exchange rate
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public float getExchangeRate(String fromCurrency, String toCurrency, int year, int month, int day)
			throws IOException, ParserConfigurationException, SAXException {
		
		String m = month + "";
		String d = day + "";
		
		if(m.length() < 2){
			m = 0 + m;
		}
		
		if(d.length() < 2){
			d = 0 + d;
		}
		
		urlString += year + "/" + m + "/" + d + ".xml";
		url = new URL(urlString);
		xmlStream = url.openStream();
		XMLReader reader = new XMLReader();
		return reader.findRate(fromCurrency, toCurrency, xmlStream);
	}

	private class XMLReader {
		
		/**
		 * Reads an XML file and extracts a currency's exchange rate against the euro
		 * @param currencyCode the currency code for the desired currency
		 * @param XMLStream the InputStream for the webpage's XML file
		 * @return returns a float which is the exchange rate for currencyCode against the euro
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 * @throws IOException
		 */
		private float findRate(String currencyCode, InputStream XMLStream)
				throws ParserConfigurationException, SAXException, IOException {
			Document doc = createDocument(XMLStream);
			NodeList nodes = doc.getElementsByTagName("fx");
			float result = 0;
			for (int i = 0; i < nodes.getLength(); i++) {
				Element node = (Element) nodes.item(i);
				NodeList codes = node.getElementsByTagName("currency_code");
				Element firstElement = (Element) codes.item(0);
				NodeList children = firstElement.getChildNodes();
				String value = children.item(0).getNodeValue();

				if (value.equals(currencyCode)) {
					codes = node.getElementsByTagName("rate");
					firstElement = (Element) codes.item(0);
					children = firstElement.getChildNodes();
					result = Float.parseFloat(children.item(0).getNodeValue());
					break;
				}
			}

			return result;

		}
		
		/**
		 * Reads an XML file and extracts a float
		 * @param currencyCode1 the first currency code for the desired currency
		 * @param currencyCode2 the second currency code for the desired currency
		 * @param XMLStream the input stream for the webpage's XML file
		 * @return a float, the exchange rate for currencyCode1 / the exchange rate for currencyCode2
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 * @throws IOException
		 */
		private float findRate(String currencyCode1, String currencyCode2, InputStream XMLStream)
				throws ParserConfigurationException, SAXException, IOException {
			Document doc = createDocument(XMLStream);
			NodeList nodes = doc.getElementsByTagName("fx");
			float rate1 = -1;
			float rate2 = -1;
			for (int i = 0; i < nodes.getLength(); i++) {

				if (rate1 != -1 && rate2 != -1) {
					break;
				}

				Element node = (Element) nodes.item(i);
				NodeList codes = node.getElementsByTagName("currency_code");
				Element firstElement = (Element) codes.item(0);
				NodeList children = firstElement.getChildNodes();
				String value = children.item(0).getNodeValue();

				if (value.equals(currencyCode1)) {
					codes = node.getElementsByTagName("rate");
					firstElement = (Element) codes.item(0);
					children = firstElement.getChildNodes();
					rate1 = Float.parseFloat(children.item(0).getNodeValue());
				}

				if (value.equals(currencyCode2)) {
					codes = node.getElementsByTagName("rate");
					firstElement = (Element) codes.item(0);
					children = firstElement.getChildNodes();
					rate2 = Float.parseFloat(children.item(0).getNodeValue());
				}
			}
			return rate1 / rate2;
		}

		private Document createDocument(InputStream XMLStream)
				throws ParserConfigurationException, SAXException, IOException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(XMLStream);
			return doc;
		}
	}
}