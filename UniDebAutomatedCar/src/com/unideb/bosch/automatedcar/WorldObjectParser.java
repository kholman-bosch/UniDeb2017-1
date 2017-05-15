package com.unideb.bosch.automatedcar;

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
	private static int width = 0;
	private static int height = 0;
	private static final int XML_OFFSET_X = 100;
	private static final int XML_OFFSET_Y = 100;
	private static ArrayList<WorldObject> worldObjects = new ArrayList<WorldObject>();

	private WorldObjectParser() {
	};

	public static void initWorld() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(world);
			Element scene = doc.getDocumentElement();

			width = Integer.parseInt(scene.getAttributes().getNamedItem("width").getTextContent());
			height = Integer.parseInt(scene.getAttributes().getNamedItem("height").getTextContent());

			NodeList objects = scene.getElementsByTagName("Object");

			for (int i = 0; i < objects.getLength(); i++) {
				Element e = (Element) (objects.item(i));
				int x = Integer.parseInt(e.getElementsByTagName("Position").item(0).getAttributes().getNamedItem("x").getTextContent().toString());
				int y = Integer.parseInt(e.getElementsByTagName("Position").item(0).getAttributes().getNamedItem("y").getTextContent().toString());
				// all elements of the 2x2 matrix contain the theta only in different ways
				// I extract the rotation from the first component by doint an invers cosine
				float rot = Float.parseFloat((e.getElementsByTagName("Transform").item(0).getAttributes().getNamedItem("m11").getTextContent().toString()));
				String typef = e.getAttributes().getNamedItem("type").getTextContent().toString();
				float objRadius = getObjectRadius(typef);
				int objMidPointX = x - XML_OFFSET_X + (int) objRadius;
				int objMidPointY = y - XML_OFFSET_Y + (int) objRadius;
				WorldObject object = new WorldObject(objMidPointX, objMidPointY, (float) (Math.acos(rot)), typef, objRadius);
				worldObjects.add(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addCarToTheDatabase(WorldObject carsWorldObject_Reference){
		worldObjects.add(carsWorldObject_Reference);
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static ArrayList<WorldObject> getWorldObjects() {
		return worldObjects;
	}

	private static float getObjectRadius(String o_type) {
		switch (o_type) {
		case "tree":
			return 150f / 2f;
		case "man":
			return 80f / 2f;
		case "parking_bollard":
			return 80f / 2f;
		default:
			if (o_type.contains("sign")) {
				return 80f / 2f;
			}
			break;
		}
		return 1f;
	}
}
