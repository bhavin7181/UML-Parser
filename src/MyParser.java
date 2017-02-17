import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class MyParser {
	
	private static Map<String,String> yUMLMap = new HashMap<String,String>();
	private final static String PARENT_CLASS="-^";
	private final static String INTERFACE="-.-^";
	
	static{
		yUMLMap.put("public", "+");
		yUMLMap.put("private", "-");
	}

	public static void main(String args[]) throws Exception
	{
		//for(int i=1;i<6;i++)
		//{
			FileData fileData = new FileData();
			String pathString = args[0];
			//String pathString = "D:/SJSU/Acad/202/Assignments/Uml Parser/Test cases C#/uml-parser-test-"+1;
			String[] folderArray = pathString.split("/");
			String fileName = folderArray[folderArray.length-1];
			Files.walk(Paths.get(pathString)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".cs")) {
			    	try {
						visitTree(filePath.toString(),fileData);
					} catch (Exception e) {
						e.printStackTrace();
					}
			        //System.out.println(filePath);
			    }
			});
			String yUmlInput = getYUMLInput(fileData);
			yUmlInput = yUmlInput.replace("-uses-.->", "uses-.->");
			yUmlInput = yUmlInput.replace("--","-");
			////System.out.println("File Data :"+fileData.toString());
			System.out.println("yUmlInput "+yUmlInput);
			saveOutputImageFile(yUmlInput, args[1]);
			//saveOutputImageFile(yUmlInput, "D:/SJSU/Acad/202/Assignments/Uml Parser/output/"+fileName+".png");
		//}
	}
	
	private static void visitTree(String filePath,FileData fileData) throws FileNotFoundException, IOException
	{
		ANTLRInputStream inputStream = new ANTLRInputStream(new FileInputStream(filePath));
		CSharp4Lexer helloLexer = new CSharp4Lexer(inputStream);
		CommonTokenStream commonTokenStream = new CommonTokenStream(helloLexer);
		CSharp4Parser helloParser = new CSharp4Parser(commonTokenStream);
		ParseTree tree = helloParser.compilation_unit();
		ParseTreeWalker walker = new ParseTreeWalker();
		MyListener treeListner = new MyListener(helloParser,fileData);
		walker.walk(treeListner, tree);
	}
	
	private static String getYUMLInput(FileData fileData)
	{
		StringBuffer yUmlInput = new StringBuffer();
		/*StringBuffer classInput = null;
		List<AttributeInfo> attributes = null;
		List<MethodInfo> methods = null;
		ClassInfo visitingClass=null;
		Map<String,String> relationMap = null;*/
		Map<String,Map<String,String>> classRelationMap = new HashMap<String,Map<String,String>>();
		Map<String,String> classYUmlStringMap = new HashMap<String,String>();
		Map<String, ClassInfo> classMap = fileData.getClassMap();
		Map<String,Boolean> isClassVisitedMap = new HashMap<String,Boolean>();
		
		traverseClassMap(classRelationMap,classYUmlStringMap,classMap,fileData.getInterfaceMap());
		traverseInterfaceMap(classYUmlStringMap,fileData.getInterfaceMap(),classRelationMap);
		
		boolean isFirstIteration = true;
		for (String currentClassOrInterface : classYUmlStringMap.keySet()) 
		{
			Map<String, String> relations = classRelationMap.get(currentClassOrInterface);
			//System.out.println("Current class or interface : "+currentClassOrInterface+" yUml :"+yUmlInput.toString());
			if(relations!=null)
			{
				for (String relatedClassOrInterface : relations.keySet()) 
				{
					if(!isFirstIteration)
						yUmlInput.append(",");
					if(isClassVisitedMap.get(currentClassOrInterface)==null)
					{
						isClassVisitedMap.put(currentClassOrInterface, true);
						yUmlInput.append(classYUmlStringMap.get(currentClassOrInterface));
					}
					else
					{
						//yUmlInput.append("["+currentClassOrInterface+"]");
						yUmlInput = appendClassOrInterfaceUml(fileData, yUmlInput, currentClassOrInterface,classYUmlStringMap);
					}
					if(classRelationMap.get(relatedClassOrInterface)!=null && classRelationMap.get(relatedClassOrInterface).get(currentClassOrInterface)!=null)
					{
						String rel = classRelationMap.get(relatedClassOrInterface).get(currentClassOrInterface);
						if(rel!=null)
						{
							yUmlInput = appendAllRelations(fileData,yUmlInput, rel, currentClassOrInterface, relatedClassOrInterface,classYUmlStringMap);
						}
						
						classRelationMap.get(relatedClassOrInterface).remove(currentClassOrInterface);
					}
					if(!relations.get(relatedClassOrInterface).equals("-.-^") &&  !relations.get(relatedClassOrInterface).equals("uses-.->")
							&&  !relations.get(relatedClassOrInterface).equals("-^"))
						yUmlInput.append("-");
					//yUmlInput.append(relations.get(relatedClassOrInterface)).append(classYUmlStringMap.get(relatedClassOrInterface));
					yUmlInput = appendAllRelations(fileData,yUmlInput, relations.get(relatedClassOrInterface), currentClassOrInterface, relatedClassOrInterface,classYUmlStringMap);
					yUmlInput.append(classYUmlStringMap.get(relatedClassOrInterface));
					isFirstIteration = false;
				}
			}
			//System.out.println("Current class or interface : "+currentClassOrInterface+" yUml :"+yUmlInput.toString());
		}
		return yUmlInput.toString();
	}
	
	private static StringBuffer appendAllRelations(FileData fileData,StringBuffer yUmlInput,String rel,String currentClassOrInterface,String relatedClassOrInterface,Map<String,String> classYUmlStringMap)
	{
		if(rel.equals("1") || rel.equals("0..*") || rel.split("~~").length==1)
			yUmlInput.append(rel);
		else if(rel.split("~~").length>1)
		{
			String[] relArr = rel.split("~~");
			for (String relstr : relArr) {
				if(!relstr.equals("-.-^") &&  !relstr.equals("uses-.->")
						&&  !relstr.equals("-^"))
					yUmlInput.append("-");
				yUmlInput.append(relstr);
				yUmlInput = appendClassOrInterfaceUml(fileData, yUmlInput, relatedClassOrInterface,classYUmlStringMap);
				yUmlInput.append(",");
				yUmlInput = appendClassOrInterfaceUml(fileData, yUmlInput, currentClassOrInterface,classYUmlStringMap);
			}
		}
		return yUmlInput;
	}
	
	private static StringBuffer appendClassOrInterfaceUml(FileData fileData,StringBuffer yUmlInput,String currentClassOrInterface,Map<String, String> classYUmlStringMap)
	{
		if(fileData.getClassMap().get(currentClassOrInterface)!=null)
			yUmlInput.append(classYUmlStringMap.get(currentClassOrInterface));//yUmlInput.append("["+currentClassOrInterface+"]");
		else if(fileData.getInterfaceMap().get(currentClassOrInterface)!=null)
			yUmlInput.append(classYUmlStringMap.get(currentClassOrInterface));//yUmlInput.append("[＜＜interface＞＞;"+currentClassOrInterface+"]");
		return yUmlInput;
	}
	
	private static void traverseInterfaceMap(Map<String, String> classYUmlStringMap,Map<String,InterfaceInfo> interfaceMap, Map<String, Map<String, String>> classRelationMap) 
	{
		String interfaceName = null;
		StringBuffer interfaceInput = null;
		Map<String, String> relationMap = null;
		if(interfaceMap.size()>0)
		{
			Set<String> keySet = interfaceMap.keySet();
			for (String iName : keySet) 
			{
				interfaceInput = new StringBuffer();
				interfaceName = interfaceMap.get(iName).getName();
				interfaceInput.append("[＜＜interface＞＞;"+interfaceName+"|");
				if(interfaceMap.get(iName).getImplementers().size()>0)
				{
					relationMap = new HashMap<String, String>();
					for (ClassInfo impl : interfaceMap.get(iName).getImplementers()) {
						/*relationMap.put(impl.getName(), "^-.-");*/
					}
				}
				
				if(interfaceMap.get(iName).getAttributes().size()>0)
				{
					for(AttributeInfo attr : interfaceMap.get(iName).getAttributes())
					{
						if(yUMLMap.get(attr.getAccessModifier())!=null)
						{
							interfaceInput.append(yUMLMap.get(attr.getAccessModifier())+attr.getName()+":"+attr.getDataType());
							if(!Utility.isNullOrEmptyString(attr.getRankSpecifier()))
								interfaceInput.append(attr.getRankSpecifier().equals("[]")?"(*)":"");	
							interfaceInput.append(";");
						}
					}
				}
				
				interfaceInput.append("|");
				for(MethodInfo method : interfaceMap.get(iName).getMethods())
				{
					if(method!=null)
					{
							interfaceInput.append(yUMLMap.get(method.getAccessModifier())).append(method.getName()).append("(");
							if(method.getArguments().size()>0)
							{
								AttributeInfo param = null;
								for (int m=0;m<method.getArguments().size();m++) {
									param = method.getArguments().get(m);
									if(m>0)
										interfaceInput.append(";");
									interfaceInput.append(param.getName()+":"+param.getDataType());
								}
							}
							interfaceInput.append(")");
							if(!Utility.isNullOrEmptyString(method.getReturnType()))
							{
								interfaceInput.append(":"+method.getReturnType()+";");
							}
						}
				}
				classYUmlStringMap.put(interfaceName, interfaceInput.append("]").toString());
				classRelationMap.put(interfaceName, relationMap);
			}
		}
	}

	private static void appendRelationIfAlreadyExists(Map<String,String> relationMap,String classOrInterface,String relation)
	{
		if(relationMap.get(classOrInterface)==null)
			relationMap.put(classOrInterface,relation);
		else if(!relationMap.get(classOrInterface).contains(relation))
			relationMap.put(classOrInterface,relationMap.get(classOrInterface)+"~~"+relation);
		//relationMap.put(classOrInterface,relation);
	}
	
	private static void traverseClassMap(Map<String, Map<String, String>> classRelationMap,Map<String, String> classYUmlStringMap, Map<String, ClassInfo> classMap,Map<String,InterfaceInfo> interfaceMap) 
	{
		StringBuffer classInput = null;
		List<AttributeInfo> attributes = null;
		List<MethodInfo> methods = null;
		ClassInfo visitingClass=null;
		Map<String,String> relationMap = null;

		for (String className : classMap.keySet()) 
		{
			for(String iKey : interfaceMap.keySet())
			{
				InterfaceInfo interfaceInfo = interfaceMap.get(iKey);
				if(classMap.get(className).getParentClass()!=null && classMap.get(className).getParentClass().getName().equals(interfaceInfo.getName()))
				{
					classMap.get(className).setParentClass(null);
					classMap.get(className).getInterfaces().add(interfaceInfo);
				}
			}
			
			classInput = new StringBuffer();
			relationMap = new HashMap<String,String>();
			visitingClass = classMap.get(className);
			
			List<InterfaceInfo> interfaceList = visitingClass.getInterfaces();
			List<AttributeInfo> visitingClassUpdatedAttr = new ArrayList<>();
			List<MethodInfo> visitingClassUpdatedMethods = new ArrayList<>();
			for(int l=0;l<visitingClass.getInterfaces().size();l++)
			{
				InterfaceInfo iInfo = interfaceList.get(l);
				//relationMap.put(iInfo.getName(), "-.-^");
				appendRelationIfAlreadyExists(relationMap, iInfo.getName(), "-.-^");
				//asdsads
				//remove matching attributes from class
				for(AttributeInfo cAttr : visitingClass.getAttributes())
				{
					boolean classAttrMatches=false;
					for(AttributeInfo iAttr : iInfo.getAttributes())
					{
						if(iAttr.equals(cAttr))
							classAttrMatches=true;
					}
					if(!classAttrMatches)
						visitingClassUpdatedAttr.add(cAttr);
				}
				visitingClass.setAttributes(visitingClassUpdatedAttr);
				visitingClassUpdatedAttr = new ArrayList<>();
				
				//remove matching methods from class
				for(MethodInfo cMethod : visitingClass.getMethods())
				{
					/*boolean classMethodMatches=false;
					for(MethodInfo iMethod : iInfo.getMethods())
					{
						if(iMethod.equals(cMethod))
							classMethodMatches=true;
					}
					if(!classMethodMatches)
						visitingClassUpdatedMethods.add(cMethod);
					*/
					
					boolean showMethod=true;
					for(MethodInfo iMethod : iInfo.getMethods())
					{
						//System.out.println("cMethod "+cMethod.getName());
						if(iMethod.equals(cMethod))
						{
							showMethod=false;
							break;
						}
						/*else
							cMethod.setShowMethod(true);*/
							//classMethodMatches=true;
					}
					//if(!classMethodMatches)
					cMethod.setShowMethod(showMethod);
					visitingClassUpdatedMethods.add(cMethod);
				}
				visitingClass.setMethods(visitingClassUpdatedMethods);
				visitingClassUpdatedMethods = new ArrayList<>();
			}
			
			classInput.append("[").append(className);
			attributes = visitingClass.getAttributes();
			//System.out.println("executing Class "+className);
			classInput.append("|");
			if(!attributes.isEmpty())
			{
				for (AttributeInfo attr : attributes) 
				{
					if(classMap.get(attr.getDataType())!=null || classMap.get(attr.getDataTypeArg())!=null)
					{
						//System.out.println("Arg : "+attr.getDataTypeArg()+"\n"+"Data Type: "+attr.getDataType());
						if(classMap.get(attr.getDataType())!=null)
						{
							relationMap.put(attr.getDataType(),"1");
						}
						else if(attr.getDataType().equals("ICollection"))
						{
							relationMap.put(attr.getDataTypeArg(),"0..*");
						}
						
					}
					else if(interfaceMap.get(attr.getDataType())!=null || interfaceMap.get(attr.getDataTypeArg())!=null)
					{
						if(interfaceMap.get(attr.getDataType())!=null)
						{
							//relationMap.put(attr.getDataType(),"1");
							//System.out.println("attr.getDataType() 1111"+attr.getDataType());
							//relationMap.put(attr.getDataType(),"uses-.->");
							//appendRelationIfAlreadyExists(relationMap,attr.getDataType(),"-.->");
							appendRelationIfAlreadyExists(relationMap,attr.getDataType(),"1");
						}
						else if(attr.getDataType().equals("ICollection") && interfaceMap.get(attr.getDataTypeArg())!=null)
						{
							appendRelationIfAlreadyExists(relationMap,attr.getDataTypeArg(),"0..*");
						}
						else if(interfaceMap.get(attr.getDataTypeArg())!=null)
						{
							//relationMap.put(attr.getDataTypeArg(),"uses-.->");
							appendRelationIfAlreadyExists(relationMap,attr.getDataTypeArg(),"-.->");
						}
					}
					else
					{
						if(yUMLMap.get(attr.getAccessModifier())!=null)
						{
							classInput.append(yUMLMap.get(attr.getAccessModifier())+attr.getName()+":"+attr.getDataType());
							if(!Utility.isNullOrEmptyString(attr.getRankSpecifier()))
								classInput.append(attr.getRankSpecifier().equals("[]")?"(*)":"");	
							classInput.append(";");
						}
						/*if(count!=attributes.size()-1)
							classInput.append(";");*/
					}
				}
			}
			methods = visitingClass.getMethods();
			if(methods.size()>0)
			{
				classInput.append("|");
				int k=0;
				for (MethodInfo methodInfo : methods) 
				{
					if(!methodInfo.getAccessModifier().equals("private"))
					{
						if(k>0 && !classInput.toString().endsWith(";"))
							if(methodInfo.isShowMethod()) classInput.append(";");
						k++;
						if(yUMLMap.get(methodInfo.getAccessModifier())!=null)
						{
							if(methodInfo.isShowMethod()) classInput.append(yUMLMap.get(methodInfo.getAccessModifier())).append(methodInfo.getName()).append("(");
							if(methodInfo.getArguments().size()>0)
							{
								AttributeInfo param = null;
								for (int m=0;m<methodInfo.getArguments().size();m++) {
									param = methodInfo.getArguments().get(m);
									if(m>0)
										if(methodInfo.isShowMethod()) classInput.append(";");
									if(methodInfo.isShowMethod()) classInput.append(param.getName()+":"+param.getDataType());
									if(!Utility.isNullOrEmptyString(param.getRankSpecifier()))
										if(methodInfo.isShowMethod()) classInput.append(param.getRankSpecifier().equals("[]")?"(*)":"");	
									if(interfaceMap.containsKey(param.getDataType()) && (relationMap.get(param.getDataType())==null || !relationMap.get(param.getDataType()).contains(methodInfo.getName().equals(visitingClass.getName())?"-.->":"uses-.->")))
									{
										boolean isAttrAlsoOrConstructor = false;
										for(int p=0;p<attributes.size();p++)
										{
											if(param.getDataType().equals(attributes.get(p).getDataType()))
											{
												isAttrAlsoOrConstructor = true;
												break;
											}
										}
										//relationMap.put(param.getDataType(), "uses-.->");
										appendRelationIfAlreadyExists(relationMap,param.getDataType(), isAttrAlsoOrConstructor || methodInfo.getName().equals(visitingClass.getName())?"-.->":"uses-.->");
									}
								}
							}
							
							if(methodInfo.getMethodBodyComponentList().size()>0)
							{
								String componentClassOrInterface = null;
								for (int m=0;m<methodInfo.getMethodBodyComponentList().size();m++) {
									componentClassOrInterface = methodInfo.getMethodBodyComponentList().get(m);
									if(interfaceMap.containsKey(componentClassOrInterface) && relationMap.get(componentClassOrInterface)==null)
									{
										//relationMap.put(compomentClassOrInterface, "uses-.->");
										appendRelationIfAlreadyExists(relationMap,componentClassOrInterface, "-.->");
									}
								}
							}
							
							if(methodInfo.isShowMethod()) classInput.append(")");
							if(!Utility.isNullOrEmptyString(methodInfo.getReturnType()))
							{
								if(methodInfo.isShowMethod()) classInput.append(":"+methodInfo.getReturnType()+";");
							}
							else
							{
								if(methodInfo.isShowMethod()) classInput.append(";");
							}
						}
					}
				}
			}
			
			if(visitingClass.getParentClass()!=null && !Utility.isNullOrEmptyString(visitingClass.getParentClass().getName()))
			{
				String rel = MyParser.PARENT_CLASS;
				if(interfaceMap.containsKey(visitingClass.getParentClass().getName()))
				{
					rel = MyParser.INTERFACE;
				}
				//relationMap.put(visitingClass.getParentClass().getName(),rel);
				appendRelationIfAlreadyExists(relationMap, visitingClass.getParentClass().getName(),rel);
			}
			
			classInput.append("]");
			String classInputStr = classInput.toString();
			if(classInputStr.endsWith("|]"))
			{
				classInputStr = classInputStr.substring(0,classInputStr.length()-2)+"]";
			}
			if(classInputStr.equals("["+className+"]"))
			{
				classInputStr = "["+className+"||]";
			}
			classYUmlStringMap.put(className, classInputStr);
			classRelationMap.put(className, relationMap);
		}
	}

	private static void saveOutputImageFile(String yUmlInput,String outputPath) throws URISyntaxException
	{
		URL url ;
		try {
            url = new URL("http://yuml.me/diagram/plain/class/" + URLEncoder.encode(yUmlInput,"UTF-8") + ".png");
            //System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error : " + conn.getResponseCode());
            }
            //OutputStream outputStream = new FileOutputStream(new File("D:/SJSU/Acad/202/Assignments/Uml Parser/output/"+fileName+".png"));
            OutputStream outputStream = new FileOutputStream(new File(outputPath));
            //OutputStream outputStream = new FileOutputStream(new File("D:/SJSU/Acad/202/Assignments/Uml Parser/output/out.png"));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = conn.getInputStream().read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
		
}
