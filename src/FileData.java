import java.util.HashMap;
import java.util.Map;

public class FileData {
	
	private Map<String,ClassInfo> classMap;
	private Map<String,InterfaceInfo> interfaceMap;

	public FileData()
	{
		classMap = new HashMap<String,ClassInfo>();
		setInterfaceMap(new HashMap<String,InterfaceInfo>());
	}
	
	public Map<String, ClassInfo> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<String, ClassInfo> classMap) {
		this.classMap = classMap;
	}

	public Map<String,InterfaceInfo> getInterfaceMap() {
		return interfaceMap;
	}

	public void setInterfaceMap(Map<String,InterfaceInfo> interfaceMap) {
		this.interfaceMap = interfaceMap;
	}

	public String toString()
	{
		String str = "File Data : ";
		for (String className : classMap.keySet()) {
			str=str+"\n"+className+": \n";
			ClassInfo class1 = classMap.get(className);
			if(class1!=null)
			{
			for (AttributeInfo attr : class1.getAttributes()) {
				str = str+attr.getAccessModifier()+" "+attr.getDataType()+" "+attr.getName()+"\n";
			}
			str=str+"\n"+"Methods"+": \n";
			for (MethodInfo m : class1.getMethods()) {
				str = str+m.getAccessModifier()+" "+m.getName()+"\n";
			}
			
			str = str+"\nInterfaces :"+"\n";
			for (InterfaceInfo m : class1.getInterfaces()) {
				str = str+m.getAccessModifier()+" "+m.getName()+"\n";
			}
			}
			
		}
		str=str+"\n Interfaces : \n";
		/*for (String iName : interfaceMap.keySet()) {
			str=str+"\n"+iName+": \n";
			InterfaceInfo class1 = interfaceMap.get(iName);
			if(class1!=null)
			{
				for (AttributeInfo attr : class1.getAttributes()) {
					str = str+attr.getAccessModifier()+" "+attr.getDataType()+" "+attr.getName()+"\n";
				}
			}
		}*/
		return str;
	}
}