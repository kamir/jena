package org.apache.jena.fuseki;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * 
 * Fuseki-Service Configuration Utility
 * 
 * This tool is used to create a configuration file for
 * a FUSEKI server cluster. We create it locally and use
 * ZOOKEEPER to deploy it to all FUSEKI services.
 * 
 * @author kamir
 */
public class FusekiServiceConfigurator {

	private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final Logger LOG = Logger.getLogger(FusekiServiceConfigurator.class.getName());

		public static void addDomainServerGroup(String jbossHome,
			String serverGroupName) {
		try {

			String fileName = String.format(
					"%s%sdomain%sconfiguration%sdomain.xml", jbossHome,
					File.separator, File.separator, File.separator);
			LOG.info(String.format("Adding server group %s to %s",
					serverGroupName, fileName));

			File file = new File(fileName);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);

			Element root = document.getDocumentElement();

			Element serverGroups = document.createElement("server-groups");

			Element serverGroup = document.createElement("server-group");
			serverGroup.setAttribute("name", serverGroupName);
			serverGroup.setAttribute("profile", "full-ha");

			Element jvm = document.createElement("jvm");
			jvm.setAttribute("name", "default");

			Element heap = document.createElement("heap");
			heap.setAttribute("size", "64m");
			heap.setAttribute("max-size", "512m");

			Element socketBindingGroup = document
					.createElement("socket-binding-group");
			socketBindingGroup.setAttribute("ref", "full-sockets");

			jvm.appendChild(heap);
			serverGroup.appendChild(jvm);
			serverGroup.appendChild(socketBindingGroup);
			serverGroups.appendChild(serverGroup);
			root.appendChild(serverGroups);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Problem creating new server", e);
		}
	}

	public static void addDomainServer(String jbossHome,
			String serverGroupName, String serverName, int portOffset) {
		try {
			String fileName = String.format(
					"%s%sdomain%sconfiguration%shost.xml", jbossHome,
					File.separator, File.separator, File.separator);
			LOG.info(String
					.format("Adding server %s in server group %s with port offset %s to %s",
							serverName, serverGroupName, portOffset, fileName));

			File file = new File(fileName);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);

			Element root = document.getDocumentElement();

			Element servers = document.createElement("servers");

			Element server = document.createElement("server");
			server.setAttribute("name", serverName);
			server.setAttribute("group", serverGroupName);

			Element socketBindings = document.createElement("socket-bindings");
			socketBindings.setAttribute("port-offset",
					String.valueOf(portOffset));

			server.appendChild(socketBindings);
			servers.appendChild(server);
			root.appendChild(servers);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);

			transformer.transform(source, result);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Problem creating new server", e);
		}
	}

	
	private static void closeCloseable(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				LOG.info("Problem closing closeable");
			}
		}
	}

	private static String hostToIP(String host) {
		String ipAddr = "";
		try {
			InetAddress inetAddr = InetAddress.getByName(host);

			byte[] addr = inetAddr.getAddress();

			for (int i = 0; i < addr.length; i++) {
				if (i > 0) {
					ipAddr += ".";
				}
				ipAddr += addr[i] & 0xFF;
			}
		} catch (UnknownHostException e) {
			LOG.log(Level.SEVERE, "Problem converting " + host
					+ " to IP address", e);
		}
		return ipAddr;
	}
}
