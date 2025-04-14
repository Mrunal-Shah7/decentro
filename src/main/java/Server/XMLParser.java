package Server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.concurrent.ConcurrentHashMap;

public class XMLParser {
    private String configPath;
    private ConcurrentHashMap<String, String> servers;
    private ConcurrentHashMap<String, String> serverNames;

    public XMLParser(String configPath) {
        this.configPath = configPath;
        this.servers = new ConcurrentHashMap<>();
        this.serverNames = new ConcurrentHashMap<>();
        parseConfig();
    }

    private void parseConfig() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(configPath);
            doc.getDocumentElement().normalize();

            NodeList serverList = doc.getElementsByTagName("server");
            for (int i = 0; i < serverList.getLength(); i++) {
                Element server = (Element) serverList.item(i);
                String id = server.getAttribute("id");
                String ip = server.getElementsByTagName("ip").item(0).getTextContent();
                String port = server.getElementsByTagName("port").item(0).getTextContent();
                servers.put(id, ip + ":" + port);
                serverNames.put(id, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerIP(String serverId) {
        String[] parts = servers.get(serverId).split(":");
        return parts[0];
    }

    public int getServerPort(String serverId) {
        String[] parts = servers.get(serverId).split(":");
        return Integer.parseInt(parts[1]);
    }

    public ConcurrentHashMap<String, String> getServers() {
        return servers;
    }

    public ConcurrentHashMap<String, String> getServerNames() {
        return serverNames;
    }
}