//Item that can navigate/display lore pages
//requires a separate JSON

package com.amelithic.zorkgame.items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

interface Readable {
    String getInitialMessage();
    Map<Integer, String[]> getPages();
    String[] readPage(int pageId);
}

public class InfoItem extends Item implements Readable, Usable {
    private Path infoPath;
    private String initialMessage;
    private Map<Integer,String[]> pages; //pageId -> [pageName, pageContent]

    private static ObjectMapper objmap = getDefaultObjectMapper(); //for JSON parsing

    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        //config ...
        return defaultObjectMapper;
    }

    public static JsonNode parse(String src) throws IOException {
        return objmap.readTree(src);
    }

    public InfoItem(String id, String name, String description, boolean isPortable, Path infoPath) {
        super(id, name, description, isPortable);
        this.infoPath = infoPath;

        try {
            String infoFileStr = Files.readString(infoPath);
            System.out.println(infoFileStr); //debug
            JsonNode lore = parse(infoFileStr);

            this.initialMessage = lore.get("intialMessage").asText();

            //initialise pages map
            this.pages = new HashMap<>();
            ArrayNode pagesArrayFromFile = (ArrayNode) lore.get("pages");
            for (int i=0; i < pagesArrayFromFile.size(); i++) {
                Integer pageId = pagesArrayFromFile.get(i).get("pageId").asInt();
                String pageName = pagesArrayFromFile.get(i).get("pageName").asText();
                String pageContent = pagesArrayFromFile.get(i).get("pageContent").asText();

                this.pages.put(pageId, new String[]{pageName,pageContent});
            }
        } catch (IOException e) {
            System.err.println("Exception when reading the JSON item info file...");
            e.printStackTrace();
        }
    }

    @Override
    public String getInitialMessage() {
        return initialMessage;
    }

    @Override
    public Map<Integer, String[]> getPages() {
        return pages;
    }

    @Override
    public String[] readPage(int pageId) {
        return pages.get(pageId);
    }

    @Override
    public String use() {
        return "yay";
    }
    
}
