package org.ancit.bitbucket.utils.pr;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class BitBucketRestClient {
	public static void main(String[] args) throws Exception {

		String Url = "https://bitbucket.org/api/2.0/repositories/annamalai_chockalingam/message-translators/pullrequests/";
		String xmlFile = "{\"title\":\"Sample\",\"description\":\"Sample	\",\"source\":\"{\"branch\":\"{\"name\":\"master\"},\"repository\":\"{\"full_name\":\"subramanyamcs/message_translators\"}},\"destination\":\"{\"branch\":\"{\"name\":\"master\"}}}";
		String login = "subramanyamcs";
				String password = "l0g1n2bit";
		

		createPR(Url, xmlFile, login, password);
	}

	public static void createPR(String Url, String jsonContent, String login,
			String password) throws MalformedURLException, IOException,
			FileNotFoundException, ProtocolException {
		String SOAPAction = "";

		// Create the connection where we're going to send the file.
		URL url = new URL(Url);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;

		ByteArrayInputStream fin = new ByteArrayInputStream(jsonContent.getBytes());

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		// Copy the SOAP file to the open connection.
		copy(fin, bout);
		fin.close();

		byte[] b = bout.toByteArray();

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "application/json");
		httpConn.setRequestProperty("Accept", "application/json");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		
		String encoded = DatatypeConverter.printBase64Binary((login+":"+password).getBytes());
		httpConn.setRequestProperty ("Authorization", "Basic " + encoded);
		
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		// Everything's set up; send the XML that was read in to b.
		OutputStream out = httpConn.getOutputStream();
		out.write(b);
		out.close();

		// Read the response and write it to standard out.

		try {
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);

			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Message", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1)
						break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}