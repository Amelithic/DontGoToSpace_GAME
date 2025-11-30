package com.amelithic.zorkgame;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SaveManager {
    //fields
    String saveDir;
    private ArrayList<Path> saveFilePaths;

    //constructor
    public SaveManager() {
        saveDir = "/saves/";
        saveFilePaths = new ArrayList<>();

        File dir = new File(saveDir);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean created = dir.mkdir(); // creates directory if none
            if (created) {
                System.out.println("Directory created: " + dir.getAbsolutePath());
            } else {
                System.out.println("Failed to create directory: " + dir.getAbsolutePath());
            }

        }

        //anonymous class to filter only JSON files, then add to saveFilePaths array
        File[] filesInDir = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        });
        if (filesInDir != null) {
            for (File saveFile : filesInDir) {
                saveFilePaths.add(saveFile.toPath());
            }
        }
    }

    public ArrayList<Path> getSaves() {
        return saveFilePaths;
    }

    public void load(Path savePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SaveFile saveFile = mapper.readValue(savePath.toFile(), SaveFile.class);

            //Setting loaded values into Main
            Main.setPlayer(saveFile.getPlayer());
            Main.setMap(saveFile.getMap());

            System.out.println("Loaded save: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String save(Main gameState) {
        //get current date (for file name)
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        String saveFileName = saveDir+"save"+formattedDate+".json";

        //serialisation with Jackson
        ObjectMapper mapper = new ObjectMapper();

        //SaveFile class used because Main fields are static (not serialisable...)
        SaveFile saveFile = new SaveFile();
        saveFile.setPlayer(Main.getPlayer());
        saveFile.setMap(Main.getMap());

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(saveFileName), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "Save failed!";
        }
        
        return "Saved! Save file created: "+saveFileName;
    }
}
