package com.unideb.bosch.automatedcar;

import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.unideb.bosch.automatedcar.framework.WorldObject;

public class WorldObjectParser {
	
	private static final File world = new File("./world/road_1_simplified.xml");
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
						0,
						e.getAttributes().getNamedItem("type").getTextContent().toString()
						);
				worldObjects.add(object);			
			}	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	};
	
	public void drawRoadSignOrientations(Graphics2D g2) {
		// TODO implement
	}
	
	public static WorldObjectParser getInstance() {
		return instance;
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
