package com.unideb.bosch.automatedcar;

import com.unideb.bosch.automatedcar.framework.ObjectOrientation;
import com.unideb.bosch.automatedcar.framework.WorldObject;

import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class WorldObjectParser {
	
	private static final String dataBaseRoot = "./world";
	private static final File world = new File(dataBaseRoot+"/UniDebDemoScene.xml");
	private int width = 0;
	private int height = 0;
	
	ArrayList<WorldObject> worldObjects = new ArrayList<WorldObject>();
	
    // Implement the singleton pattern
	private static WorldObjectParser instance = new WorldObjectParser();
	private WorldObjectParser() {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(world);
			Element scene = doc.getDocumentElement();
			
			width = Integer.parseInt(scene.getAttributes().getNamedItem("width").getTextContent());
			height = Integer.parseInt(scene.getAttributes().getNamedItem("height").getTextContent());
			
			NodeList objects = scene.getElementsByTagName("Object");	
			
			for (int i = 0; i < objects.getLength(); i++) {
				
				Element e = (Element)(objects.item(i));
								
				WorldObject object = new WorldObject(
						Integer.parseInt(e.getElementsByTagName("Position").item(0).getAttributes().getNamedItem("x").getTextContent().toString()),
						Integer.parseInt(e.getElementsByTagName("Position").item(0).getAttributes().getNamedItem("y").getTextContent().toString()),
						// TODO determine orientation based on the rotation matrix
						ObjectOrientation.NORTH,
						e.getAttributes().getNamedItem("name").getTextContent().toString()
						);
				worldObjects.add(object);			
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	};
	
	public static WorldObjectParser getInstance() {
		return instance;
	}
	
	public static String getDataBaseRoot() {
		return dataBaseRoot;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public ArrayList<WorldObject> getWorldObjects() {
		return worldObjects;
	}
}
