import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class RWXml extends Thread {
	//constants
	private static final String PROVIDERSTR = "Provider";
	private static final String PRODUCTSTR = "Product";
	private static final String BLUEPRINTIDSTR = "BlueprintID";
	//private static final String ABS_BLUEPRINTIDSTR = "iBlueprintLibrary-cAbsoluteBlueprintID";
	private static final XPath XPATH=XPathFactory.newInstance().newXPath();
	private static final String SERZCMD="serz.exe \"";
	private static final String SERZCMD_CLOSE="\"";
	private static final XPathExpression BLUEPRINTS_XPATH; 
	private static final XPathExpression TYPES_XPATH; 
	//private static final String EMPTY_STR="";
	// end of constants
	private final File file;
	private final boolean isBin;
	//private final String xmlpath;
	private final String origFilePath;
	private final String origFileExt;
	private final int lightState;
	private static double trackHeightOffset;
	private static List<Replaceable> replace=Collections.synchronizedList(new NoDuplicateList<Replaceable>());
	private Document doc;
	private static Set<String> typelist=Collections.synchronizedSet(new HashSet<String>());
	private static List<String> selectedtypelist=new ArrayList<String>();
	private NodeList typeNodes;
	private List<Node> assetNodes= new ArrayList<Node>();
	static {
			XPathExpression bppath=null;
			XPathExpression types=null;
			try {
				bppath=XPATH.compile("//iBlueprintLibrary-cAbsoluteBlueprintID");
				types=XPATH.compile("//iBlueprintLibrary-cAbsoluteBlueprintID/..");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BLUEPRINTS_XPATH=bppath;
			TYPES_XPATH=types;
	}

	public RWXml(String path) {
		super();
		origFilePath=path;
		origFileExt =path.substring(path.lastIndexOf("."));
		lightState=3;
		if (".bin".equalsIgnoreCase(origFileExt)) {
			isBin=true;
			file = new File(origFilePath.replaceFirst(origFileExt,".xml"));
		} else {
			isBin=false;
			file = new File(origFilePath);
		}
	}
	public RWXml(String path,int lightState) {
		super();
		origFilePath=path;
		origFileExt =path.substring(path.lastIndexOf("."));
		this.lightState=lightState;
		if (".bin".equalsIgnoreCase(origFileExt)) {
			isBin=true;
			file = new File(origFilePath.replaceFirst(origFileExt,".xml"));
		} else {
			isBin=false;
			file = new File(origFilePath);
		}
	}
	
	@Override
	public void run() {
		//super.run();
		//String xmlpath=origFilePath;
		try {
			//System.out.println(xmlpath);
			if (lightState!=3 && !haveLightComponent(new File(origFilePath))) return;
			if (isBin) {
				Runtime.getRuntime().exec(SERZCMD+origFilePath+SERZCMD_CLOSE).waitFor();
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= factory.newDocumentBuilder();
			doc= builder.parse(file);
			if (lightState==3) {
				//System.out.println("parseasroute");
				typeNodes = (NodeList) TYPES_XPATH.evaluate(doc, XPathConstants.NODESET); //XPATH.evaluate("//iBlueprintLibrary-cAbsoluteBlueprintID/..", doc, XPathConstants.NODESET);
				//StringBuilder strbuild = new StringBuilder();
				//String separator= System.getProperty("line.separator");
				for (int i=0; i<typeNodes.getLength(); i++) {
					//System.out.println("nodename: ");
					typelist.add(typeNodes.item(i).getNodeName());
				}
			}
			else {
				//System.out.println("parseaslight");
				NodeList nodes = (NodeList) XPATH.evaluate("//LightComponent/*/CastShadow", doc, XPathConstants.NODESET);
				for (int i=0; i<nodes.getLength(); i++) {
					//System.out.println("node");
					if (lightState==2) {
						toggleLightShadowsCast(nodes.item(i));
						toXML();
						//System.out.println("save");
					} else {
						if (nodes.item(i).getTextContent().equals(lightState)) return;
						nodes.item(i).setTextContent(""+lightState);
						toXML();
						//System.out.println("save");
					}
				}

			}
		} catch (IOException e) {
			//throw new RuntimeException(("Could not read or convert to xml the target file! Is a serz.exe in same directory where this program is?"));
			System.out.println("Could not read or convert to xml the target file! Is a serz.exe in same directory where this program is?"+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			//throw new RuntimeException("Something bad happened at parsing xml at "+xmlpath+"! Maybe it's not a correct railworks xml/bin.",e);
			System.out.println("Something bad happened at parsing xml at "+file.getAbsolutePath()+"! Maybe it's not a correct railworks xml/bin.");
			e.printStackTrace();
		}

	}



	private Node getFirstSubElementByName(Node node,String name) {
		if (node!=null) {
			NodeList children= node.getChildNodes();
			if (children!=null) {
				for (int i=0; i<children.getLength(); i++) {
					Node child= children.item(i);
					if (child.getNodeType()==Node.ELEMENT_NODE && child.getNodeName().equals(name)) {
						return child;
					}
				}
			}
		}
		return null;
	}
	private Node getFirstSubElement(Node node) {
		if (node!=null) {
			NodeList children= node.getChildNodes();
			if (children!=null) {
				for (int i=0; i<children.getLength(); i++) {
					Node child= children.item(i);
					if (child.getNodeType()==Node.ELEMENT_NODE) {
						return child;
					}
				}
			}
		}
		return null;
	}
	private Node getFirstSubElementDepth(Node node, int depth) {
		Node currnode=node;
		for (int i=0; i<depth; i++) {
			currnode= getFirstSubElement(currnode);
			if (currnode==null) return null;
		}
		return currnode;
	}
	private Node getNthParent(Node node, int depth) {
		Node currnode=node;
		for (int i=0; i<depth; i++) {
			if (currnode==null) return null;
			currnode=currnode.getParentNode();
		}
		return currnode;
	}
	public static boolean isEmptyOrBlank(String str) {
		return str == null || str.trim().isEmpty();
	}
	public static String[] getTypeList() {
		String[] strResult=new String[typelist.size()];  
		return typelist.toArray(strResult);
	}
	public static List<String> getSelectedTypeList() {
		return selectedtypelist;
	}
	public static void setSelectedTypeList(List<String> selected) {
		selectedtypelist=selected;
	}
	public static void clearReplaceables() {
		replace.clear();
	}
	Runnable populateReplaceables = new Runnable() {
		public void run() {
			populateReplaceables();
		}
	};
	private void populateReplaceables() {
		assetNodes.clear();
		for (int j=0; j<typeNodes.getLength(); j++) {
			Node thisnode= typeNodes.item(j);
			for (String thistype:selectedtypelist) {
				if (thisnode.getNodeName().equals(thistype)) {
					Node firstsub= getFirstSubElement(thisnode);
					assetNodes.add(firstsub);
					String blueprintID = getFirstSubElementByName(firstsub,BLUEPRINTIDSTR).getTextContent();
					Node libroot= getFirstSubElement(getFirstSubElement(firstsub));
					String provider=getFirstSubElementByName(libroot,PROVIDERSTR).getTextContent();
					String product=getFirstSubElementByName(libroot,PRODUCTSTR).getTextContent();
					replace.add(new Replaceable(thistype, provider, product, blueprintID));
					break;
				}
			}
		}
		/*for (int i=0; i<selectedtypelist.size(); i++) {
			StringBuilder expressionbuilder= new StringBuilder();
			String currtype=selectedtypelist.get(i);
			
			expressionbuilder.append("//");
			expressionbuilder.append(currtype);
			expressionbuilder.append("/iBlueprintLibrary-cAbsoluteBlueprintID");

			try {
				XPathExpression expression=XPATH.compile(expressionbuilder.toString());
				NodeList nodes=(NodeList) expression.evaluate(doc, XPathConstants.NODESET);
				for (int j=0; j<nodes.getLength(); j++) {
					Node firstsub= nodes.item(j); //getFirstSubElement(nodes.item(j));
					String blueprintID = getFirstSubElementByName(firstsub,BLUEPRINTIDSTR).getTextContent();
					Node libroot= getFirstSubElement(getFirstSubElement(firstsub));
					String provider=getFirstSubElementByName(libroot,PROVIDERSTR).getTextContent();
					String product=getFirstSubElementByName(libroot,PRODUCTSTR).getTextContent();
					replace.add(new Replaceable(currtype, provider, product, blueprintID));

				}
			} catch (Exception e) {
				System.out.println("Something bad happened at populateReplaceables! Problem?");
				e.printStackTrace();
			}
		}*/

	}
	Runnable replaceAll = new Runnable() {
		public void run() {
			replaceAll();
		}
	};
	private void replaceAll() {
		boolean modified=false;
		//NodeList nods=null;
		if (assetNodes.size()==0) {
			NodeList nods;
			try {			
				nods= (NodeList) BLUEPRINTS_XPATH.evaluate(doc, XPathConstants.NODESET); 
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			for (int i=0; i<nods.getLength(); i++) {
				 assetNodes.add(nods.item(i));
			}
			
		}
		//if (nods!=null) {
			for (Node firstsub: assetNodes) {
				//Node firstsub= assetNodes.item(j); //getFirstSubElement(nodes.item(j));
				Node blueprintID=	getFirstSubElementByName(firstsub,BLUEPRINTIDSTR);
				Node libroot= getFirstSubElement(getFirstSubElement(firstsub));
				Node provider= getFirstSubElementByName(libroot,PROVIDERSTR);
				Node product= getFirstSubElementByName(libroot,PRODUCTSTR);
				String blueprintID_str=blueprintID.getTextContent();
				String provider_str=provider.getTextContent();
				String product_str=product.getTextContent();
				if (!(isEmptyOrBlank(blueprintID_str) || isEmptyOrBlank(provider_str) || isEmptyOrBlank(product_str))) {
					//Node height=getFirstSubElement(getFirstSubElement(getFirstSubElement(getFirstSubElementByName(getFirstSubElement(firstsub.getParentNode().getParentNode()),"cPosOri"))));
					Node height= getFirstSubElementDepth(getFirstSubElementByName(getFirstSubElement(firstsub.getParentNode().getParentNode()),"cPosOri"),3);
					NodeList heights=null;
					if (height==null) {
						Node main= getNthParent(firstsub, 6);
						String nodename=main.getNodeName();
						if (nodename.equals("Network-cLoftRibbon")|| nodename.equals("Network-cRoadRibbon")) {
							//System.out.println("kok:"+main.getNodeName()+"first: "+firstsub.getNodeName()+" blID:"+blueprintID.getTextContent());
							try {
								heights=(NodeList) XPATH.evaluate("Height/Network-iRibbon-cHeight/_height", main, XPathConstants.NODESET);
								/*for (int i=0; i<heights.getLength(); i++) {
									System.out.println("kisném:"+heights.item(i).getNodeName());
								}*/
							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
						}
						//System.out.println(height.getNodeName()/*+"|"+height.getTextContent()*/);
					}


					for (Replaceable curreplaceable: replace) {
						//Replaceable curreplaceable=replace.get(i);
						//System.out.println(curreplaceable);
						double objectoffset = curreplaceable.getHeightOffset();
						if (blueprintID_str.equals(curreplaceable.getCurrentBlueprintID()) && provider_str.equals(curreplaceable.getCurrentProvider()) && product_str.equals(curreplaceable.getCurrentProduct())) {

							if (objectoffset!=0.0d) {
								if (height!=null) {
									//System.out.println(Float.parseFloat(height.getTextContent())+objectoffset);
									height.setTextContent(Double.toString(Double.parseDouble(height.getTextContent())+objectoffset));
									modified=true;
								} else if (heights!=null) {

									for (int x=0; x<heights.getLength(); x++) {
										Node item= heights.item(x);
										item.setTextContent(Double.toString(Double.parseDouble(item.getTextContent())+objectoffset));
										//System.out.println("ezt:"+hig);
									}
									modified=true;
								}
								//curreplaceable.setHeightOffset(0d);
							}
							if (notEmptyStr(curreplaceable.getDesiredBlueprintID()) && notEmptyStr(curreplaceable.getDesiredProvider()) && notEmptyStr(curreplaceable.getDesiredProduct())) {
								/*if (blueprintID.getTextContent().equals(curreplaceable.getCurrentBlueprintID()) && provider.getTextContent().equals(curreplaceable.getCurrentProvider()) && product.getTextContent().equals(curreplaceable.getCurrentProduct())) {

					}*/
								blueprintID.setTextContent(curreplaceable.getDesiredBlueprintID());
								provider.setTextContent(curreplaceable.getDesiredProvider());
								product.setTextContent(curreplaceable.getDesiredProduct());
								modified=true;
								//System.out.println("modified"+j);
							}
							break;
						}
					}
				}
			}
		//}
		if (trackHeightOffset!=0.0d) {
			NodeList heightNodes=null;
			try {
				//nods= (NodeList) XPATH.evaluate("//"+curreplaceable.getType()+"/"+ABS_BLUEPRINTIDSTR+"[BlueprintID=\""+curreplaceable.getCurrentBlueprintID()+"\" and BlueprintSetID/iBlueprintLibrary-cBlueprintSetID/Product=\""+curreplaceable.getCurrentProduct()+"\" and BlueprintSetID/iBlueprintLibrary-cBlueprintSetID/Provider=\""+curreplaceable.getCurrentProvider()+"\"]", doc, XPathConstants.NODESET);
				heightNodes= (NodeList) XPATH.evaluate("/cRecordSet/Record/Network-cTrackNetwork/RibbonContainer/Network-cRibbonContainerUnstreamed/Ribbon/Network-cTrackRibbon/Height/Network-iRibbon-cHeight/_height", doc, XPathConstants.NODESET);

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			if (heightNodes!=null) {
				for (int j=0; j<heightNodes.getLength(); j++) {
					Node heightnode=heightNodes.item(j);
					try {
						heightnode.setTextContent(Double.toString((Double.parseDouble(heightnode.getTextContent())+trackHeightOffset)));
						modified=true;
						//System.out.print("newheight:"+Float.toString((Float.parseFloat(heightnode.getTextContent())+trackHeightOffset)));
					} catch (Exception e) {
						continue;
					}
				}
			}
		}
		if (modified) {
			try {
				toXML();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Replaceable repl: replace) {
				repl.setHeightOffset(0);
			}
		}
	}
	private static boolean notEmptyStr(String s) {
		return (s != null && s.length() > 1);
	}
	private void stupidKUJU() throws Exception  { // put whitespace to empty nodes, because serz.exe crashes with xml that contain void tags.
		NodeList nodes= (NodeList) XPATH.evaluate("//*[@type='cDeltaString']", doc, XPathConstants.NODESET);
		//System.out.println(nodes.getLength());
		for (int i=0; i<nodes.getLength(); i++) {
			Node currnode=  nodes.item(i);
			if (currnode.getTextContent()=="") {
				currnode.setTextContent(" ");
			}
		}
	}

	private void toXML() throws Exception
	{
		TransformerFactory xf = TransformerFactory.newInstance();
		xf.setAttribute("indent-number", new Integer(2));

		Transformer xformer = xf.newTransformer();
		xformer.setOutputProperty(OutputKeys.METHOD, "xml");
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		StringWriter strwr= new StringWriter();
		Result out = new StreamResult(strwr /*new OutputStreamWriter(fos,"UTF-8")*/);
		stupidKUJU();
		xformer.transform(new DOMSource(doc), out);
		String strout=strwr.toString().replaceAll("> <", "><");
		FileOutputStream fos= new FileOutputStream(file);
		fos.write(strout.getBytes("UTF-8"));
		fos.flush();
		fos.close();
		if (isBin) {
			Path bak= FileSystems.getDefault().getPath(origFilePath.replaceFirst(origFileExt, ".bak"));
			int i=0;
			while (bak.toFile().exists()) {
				bak= FileSystems.getDefault().getPath(origFilePath.replaceFirst(origFileExt, ".bak"+ i++));
			}
			Path origpath = FileSystems.getDefault().getPath(origFilePath);
			Files.copy(origpath, bak, StandardCopyOption.REPLACE_EXISTING);
			Runtime.getRuntime().exec(SERZCMD+file.getAbsolutePath()+SERZCMD_CLOSE).waitFor();
		}

	}

	public static synchronized List<Replaceable> getReplace() {
		return replace;
	}

	public static void writeTable(File file) throws Exception {
		String filepath= file.getAbsolutePath();
		if (!(filepath.endsWith(".xml") || filepath.endsWith(".XML"))) {
			filepath= filepath+".xml";
		}
		XMLEncoder encoder= new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filepath)));
		for (Replaceable repl : replace) {
			encoder.writeObject(repl);
		}
		//encoder.writeObject(replace);
		encoder.close();
	}
	public static void readTable(File file) throws Exception {
		XMLDecoder decoder= new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
		//replace.clear();
		try {
			while (true) {
				Object obj = decoder.readObject();
				if (obj instanceof Replaceable) {
					//replace.add((Replaceable) obj);
					Replaceable repl= (Replaceable) obj;
					replace.remove(repl);
					replace.add(repl);
				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			decoder.close();
		}
	}
	private static boolean haveLightComponent(File file) throws FileNotFoundException {
		final Scanner fileScanner = new Scanner(file);
		final Pattern pattern =  Pattern.compile("LightComponent");
		//Matcher matcher = null; 
		while(fileScanner.hasNextLine()){  
			String line = fileScanner.nextLine();  
			Matcher matcher = pattern.matcher(line);
			if(matcher.find()){
				fileScanner.close();
				return true; 
			}  

		}
		fileScanner.close();
		return false;
	}
	private void toggleLightShadowsCast(Node node) {
		if (node.getTextContent().equals("1")) {
			node.setTextContent("0");
		} else {
			node.setTextContent("1");
		}
	}
	/*public static float getTrackHeightOffset() {
		return trackHeightOffset;
	}*/
	public static void setTrackHeightOffset(double trackHeightOffset) {
		RWXml.trackHeightOffset = trackHeightOffset;
	}
	
	//64bit bigendian double HEX string
	/*private static String doubleToBigendianHEX(double in) {
		ByteBuffer buf = ByteBuffer.allocate(16);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putDouble(in);
		buf.order(ByteOrder.BIG_ENDIAN);
		return String.format("%016X",Double.doubleToLongBits(buf.getDouble(0)));
	}*/
}
