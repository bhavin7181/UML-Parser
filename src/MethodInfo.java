import java.util.ArrayList;
import java.util.List;

import org.stringtemplate.v4.STRawGroupDir;

public class MethodInfo {
	private String name;
	private String returnType;
	private String accessModifier;
	private List<AttributeInfo> arguments;
	private List<String> methodBodyComponentList;
	private boolean showMethod;
	
	public MethodInfo() {
		arguments = new ArrayList<AttributeInfo>();
		setMethodBodyComponentList(new ArrayList<String>());
		showMethod = true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getAccessModifier() {
		return accessModifier;
	}
	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}
	public List<AttributeInfo> getArguments() {
		return arguments;
	}
	public void setArguments(List<AttributeInfo> arguments) {
		this.arguments = arguments;
	}
	@Override
	public boolean equals(Object obj) {
		MethodInfo arg = (MethodInfo)obj;
		if(this.name.equals(arg.getName()) && this.returnType.equals(arg.getReturnType()) && this.accessModifier.equals(arg.getAccessModifier()) &&
				this.arguments.equals(arg.getArguments()))
				return true;
		return false;
	}
	public List<String> getMethodBodyComponentList() {
		return methodBodyComponentList;
	}
	public void setMethodBodyComponentList(List<String> methodBodyComponentList) {
		this.methodBodyComponentList = methodBodyComponentList;
	}
	public boolean isShowMethod() {
		return showMethod;
	}
	public void setShowMethod(boolean showMethod) {
		this.showMethod = showMethod;
	}
	
	
	
}
