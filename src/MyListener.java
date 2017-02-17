import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

public class MyListener extends CSharp4BaseListener {
		
		private CSharp4Parser parser;
		private FileData fileData;
		MyListener(CSharp4Parser parser,FileData fileData)
		{
			this.parser=parser;
			this.fileData=fileData;
		}

		@Override
		public void enterClass_definition(CSharp4Parser.Class_definitionContext ctx) {
				ClassInfo classInfo = new ClassInfo();
				classInfo.setName(ctx.identifier().getText());
				CSharp4Parser.Type_declarationContext typeC =(CSharp4Parser.Type_declarationContext) ctx.getParent();
				if(typeC!=null && typeC.all_member_modifiers()!=null && typeC.all_member_modifiers().all_member_modifier()!=null && typeC.all_member_modifiers().all_member_modifier().size()>0)
					classInfo.setAccessModifier(typeC.all_member_modifiers().all_member_modifier().get(0).getText());

				if(ctx.class_base()!=null)
				{
					if(ctx.class_base().class_type()!=null && ctx.class_base().class_type().type_name()!=null && ctx.class_base().class_type().type_name().namespace_or_type_name()!=null
							&& ctx.class_base().class_type().type_name().namespace_or_type_name().identifier()!=null && ctx.class_base().class_type().type_name().namespace_or_type_name().identifier().size()>0)
					{
						List<ParseTree> children = ctx.class_base().class_type().type_name().namespace_or_type_name().identifier().get(0).children;
						if(children!=null && children.size()>0 && children.get(0).getParent()!=null && children.get(0).getParent().getText()!=null)
						{
							ClassInfo parent = new ClassInfo();
							parent.setName(children.get(0).getText());
							classInfo.setParentClass(parent);
						}
					}
					
					if(ctx.class_base().interface_type()!=null && ctx.class_base().interface_type().size()>0)
					{
						InterfaceInfo interfaceInfo = null;
						List<CSharp4Parser.Interface_typeContext> interfaceContextList = ctx.class_base().interface_type();
						for (CSharp4Parser.Interface_typeContext interface_typeContext : interfaceContextList) {
							if(interface_typeContext.type_name()!=null && interface_typeContext.type_name().namespace_or_type_name()!=null)
							{
								List<CSharp4Parser.IdentifierContext> idContextList = interface_typeContext.type_name().namespace_or_type_name().identifier();
								for (CSharp4Parser.IdentifierContext identifierContext : idContextList) {
									interfaceInfo = new InterfaceInfo();
									interfaceInfo.setName(identifierContext.getText());
									classInfo.getInterfaces().add(interfaceInfo);
								}
							}
						}
					}
				}
				fileData.getClassMap().put(classInfo.getName(),classInfo);
				//System.out.println("Inside Class");
			////System.out.println(ctx.getChild(2).getChild(1).getChild(1).getText());
		}

		@Override
		public void enterClass_member_declaration(CSharp4Parser.Class_member_declarationContext ctx) 
		{
			List<String> propertyMethodList = new ArrayList<String>();
			String className = ctx.getParent().getParent().getParent().getChild(1).getText();
			//System.out.println("Inside "+className);
			if(ctx.common_member_declaration()!=null)
			{
				if(ctx.common_member_declaration().typed_member_declaration()!=null && ctx.common_member_declaration().typed_member_declaration().method_declaration2()==null)
				{
						populateAttributeInfoInClass(ctx,className,propertyMethodList);
				}
				else if(ctx.common_member_declaration().method_declaration2()!=null || ctx.common_member_declaration().constructor_declaration2()!=null
						|| (ctx.common_member_declaration().typed_member_declaration()!=null && ctx.common_member_declaration().typed_member_declaration().method_declaration2()!=null))
					populateMethodInfoInClass(ctx,className);
			}
			for (String propMethod : propertyMethodList) {
				if(!Utility.isNullOrEmptyString(propMethod))
				{
					String prop = propMethod.substring(3,propMethod.length()).toLowerCase();
					List<AttributeInfo> updatedAttrList = new ArrayList<>();
					List<AttributeInfo> originalAttrList = fileData.getClassMap().get(className).getAttributes();
					for (AttributeInfo attrInfo : originalAttrList) {
						if(attrInfo.getName().toLowerCase().equals(prop))
						{
							//updatedAttrList.remove(attrInfo);
							AttributeInfo updatedAttrInfo = attrInfo;
							updatedAttrInfo.setAccessModifier("public");
							updatedAttrList.add(updatedAttrInfo);
						}
						else
							updatedAttrList.add(attrInfo);
					}
					fileData.getClassMap().get(className).setAttributes(updatedAttrList);
				}
				
			}
			
		}
		
		private void populateMethodInfoInClass(CSharp4Parser.Class_member_declarationContext ctx, String className) 
		{
			MethodInfo methodInfo = new MethodInfo();
			if(ctx.all_member_modifiers()!=null)
			{
				methodInfo.setAccessModifier(ctx.all_member_modifiers().all_member_modifier().get(0).getText());
			}
			
			CSharp4Parser.Method_declaration2Context methodContext = ctx.common_member_declaration().method_declaration2();
			CSharp4Parser.Typed_member_declarationContext typeContext = ctx.common_member_declaration().typed_member_declaration();
			CSharp4Parser.Constructor_declaration2Context constContext = ctx.common_member_declaration().constructor_declaration2();
			List<CSharp4Parser.Fixed_parameterContext> paramList = null;
			if(methodContext!=null)
			{
				if(ctx.common_member_declaration().getChild(0)!=null)
				{
					methodInfo.setReturnType(ctx.common_member_declaration().getChild(0).getText());
				}
				if(methodContext.method_member_name()!=null)
				{
					//System.out.println("Class : "+ className + "Method Name: "+ methodContext.method_member_name().getText());
					methodInfo.setName(methodContext.method_member_name().getText());
				}
				if(methodContext.formal_parameter_list()!=null && methodContext.formal_parameter_list().fixed_parameters()!=null)
				{
					paramList = methodContext.formal_parameter_list().fixed_parameters().fixed_parameter();
				}
				
				//Method body traversal
				if(methodContext.method_body()!=null && methodContext.method_body().block()!=null 
						&& methodContext.method_body().block().statement_list()!=null && methodContext.method_body().block().statement_list().statement()!=null
						&& methodContext.method_body().block().statement_list().statement().size()>0)
				{
					List<CSharp4Parser.StatementContext> methodStmtContext = methodContext.method_body().block().statement_list().statement();
					for (CSharp4Parser.StatementContext statementContext : methodStmtContext) 
					{
						if(statementContext!=null && statementContext.declaration_statement()!=null 
								&& statementContext.declaration_statement().local_variable_declaration()!=null && statementContext.declaration_statement().local_variable_declaration().local_variable_type()!=null)
						{
							CSharp4Parser.TypeContext methodBodyTypeContext = statementContext.declaration_statement().local_variable_declaration().local_variable_type().type();
							if(methodBodyTypeContext!=null && methodBodyTypeContext.base_type()!=null && methodBodyTypeContext.base_type().class_type()!=null 
									&& methodBodyTypeContext.base_type().class_type().type_name()!=null && methodBodyTypeContext.base_type().class_type().type_name().namespace_or_type_name()!=null
									&& methodBodyTypeContext.base_type().class_type().type_name().namespace_or_type_name().identifier()!=null &&
									methodBodyTypeContext.base_type().class_type().type_name().namespace_or_type_name().identifier().size()>0)
							{
								methodInfo.getMethodBodyComponentList().add(methodBodyTypeContext.base_type().class_type().type_name().namespace_or_type_name().identifier().get(0).getText());
							}
						}
					}
				}
			}
			if(typeContext!=null)
			{
				CSharp4Parser.Method_declaration2Context methodContext2 = typeContext.method_declaration2();
				if(typeContext.type()!=null)
				{
					methodInfo.setReturnType(typeContext.type().getText());
				}
				if(methodContext2!=null && methodContext2.method_member_name()!=null)
				{
					//System.out.println("Class : "+ className + "Method Name: "+ methodContext2.method_member_name().getText());
					methodInfo.setName(methodContext2.method_member_name().getText());
				}
				if(methodContext2!=null && methodContext2.formal_parameter_list()!=null && methodContext2.formal_parameter_list().fixed_parameters()!=null)
				{
					paramList = methodContext2.formal_parameter_list().fixed_parameters().fixed_parameter();
				}
			}
			if(constContext!=null)
			{
				if(constContext.identifier()!=null)
					methodInfo.setName(constContext.identifier().getText());
				if(constContext.formal_parameter_list()!=null && constContext.formal_parameter_list().fixed_parameters()!=null)
				{
					paramList = constContext.formal_parameter_list().fixed_parameters().fixed_parameter();
				}
			}
			
				if(paramList!=null && !paramList.isEmpty())
				{
					AttributeInfo methodParam = null;
					for (CSharp4Parser.Fixed_parameterContext param : paramList) {
						if(param!=null && param.identifier()!=null)
						{
							methodParam = new AttributeInfo();
							methodParam.setName(param.identifier().getText());
							CSharp4Parser.Base_typeContext baseTypeContext = param.type().base_type();
							if(baseTypeContext!=null)
							{
								if(baseTypeContext!=null)
								{
									if(baseTypeContext.class_type()!=null && baseTypeContext.class_type().type_name()!=null)
									{
										CSharp4Parser.Namespace_or_type_nameContext namespaceOrTypeContext = baseTypeContext.class_type().type_name().namespace_or_type_name();
										methodParam.setDataType(namespaceOrTypeContext.identifier().get(0).getText());
										if(namespaceOrTypeContext.identifier().get(0).getText().equals("ICollection"))
										{
											if(namespaceOrTypeContext.type_argument_list_opt()!=null && !namespaceOrTypeContext.type_argument_list_opt().isEmpty())
											{
												if(namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments()!=null)
												{
													CSharp4Parser.Base_typeContext childBaseType = namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments().type_argument().get(0).type().base_type();
													//TODO : traverse each child
													methodParam.setDataTypeArg(childBaseType.getText());
												}
											}
										}
									}
									else if(baseTypeContext.simple_type()!=null)
									{
										methodParam.setDataType(baseTypeContext.simple_type().getText());
									}
									else if(baseTypeContext.class_type()!=null)
									{
										methodParam.setDataType(baseTypeContext.class_type().getText());
									}
								}
							}
							if(param.type().rank_specifier()!=null && param.type().rank_specifier().size()>0)
							{
								methodParam.setRankSpecifier(param.type().getChild(1).getText());
							}
					}
					methodInfo.getArguments().add(methodParam);
				}
			}
			fileData.getClassMap().get(className).getMethods().add(methodInfo);
		}

		private void populateAttributeInfoInClass(CSharp4Parser.Class_member_declarationContext ctx,String className, List<String> propertyMethodList)
		{
			if(ctx.common_member_declaration().typed_member_declaration()!=null && ctx.common_member_declaration().typed_member_declaration().method_declaration2()==null)
			{
			AttributeInfo attributeInfo = new AttributeInfo();
			
			if(ctx.common_member_declaration().typed_member_declaration()!=null && ctx.common_member_declaration().typed_member_declaration().property_declaration2()!=null)
			{
				if(ctx.common_member_declaration().typed_member_declaration().property_declaration2().member_name()!=null && ctx.common_member_declaration().typed_member_declaration().property_declaration2().member_name().getText()!=null)
				{
					String propMethod = ctx.common_member_declaration().typed_member_declaration().property_declaration2().member_name().getText();
					propertyMethodList.add(propMethod);
				}
				return;
			}
			CSharp4Parser.TypeContext typeContext = ctx.common_member_declaration().typed_member_declaration().type();
			
			if(ctx.all_member_modifiers()!=null)
			{
				attributeInfo.setAccessModifier(ctx.all_member_modifiers().all_member_modifier().get(0).getText());
			}
			
			if(typeContext!=null && typeContext.getChildCount()>1)
				attributeInfo.setRankSpecifier(typeContext.getChild(1).getText());
			
			CSharp4Parser.Base_typeContext baseTypeContext = typeContext.base_type();
			if(baseTypeContext.simple_type()!=null)
			{
				attributeInfo.setDataType(baseTypeContext.simple_type().getText());
			}
			else if(baseTypeContext.class_type()!=null)
			{
				if(baseTypeContext.class_type().type_name()!=null)
				{
					CSharp4Parser.Namespace_or_type_nameContext namespaceOrTypeContext = baseTypeContext.class_type().type_name().namespace_or_type_name();
					attributeInfo.setDataType(namespaceOrTypeContext.identifier().get(0).getText());
					if(namespaceOrTypeContext.identifier().get(0).getText().equals("ICollection"))
					{
						if(namespaceOrTypeContext.type_argument_list_opt()!=null && !namespaceOrTypeContext.type_argument_list_opt().isEmpty())
						{
							if(namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments()!=null)
							{
								CSharp4Parser.Base_typeContext childBaseType = namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments().type_argument().get(0).type().base_type();
								//TODO : traverse each child
								attributeInfo.setDataTypeArg(childBaseType.getText());
							}
						}
					}
					else
					{
						
					}
				}
				else if(!Utility.isNullOrEmptyString(baseTypeContext.class_type().getText()))
				{
					attributeInfo.setDataType(baseTypeContext.class_type().getText());
				}
			}
			if(ctx.common_member_declaration().typed_member_declaration().field_declaration2()!=null && ctx.common_member_declaration().typed_member_declaration().field_declaration2().variable_declarators()!=null
					&& ctx.common_member_declaration().typed_member_declaration().field_declaration2().variable_declarators().variable_declarator()!=null
					&&ctx.common_member_declaration().typed_member_declaration().field_declaration2().variable_declarators().variable_declarator().size()>0)
			attributeInfo.setName(ctx.common_member_declaration().typed_member_declaration().field_declaration2().variable_declarators().variable_declarator().get(0).identifier().getText());
			fileData.getClassMap().get(className).getAttributes().add(attributeInfo);
			//System.out.println("Class1 : "+className+" Attribute type : "+attributeInfo.getDataType()+" Attr Name :"+attributeInfo.getName());
		}
	}

		@Override
		public void enterInterface_member_declarations(CSharp4Parser.Interface_member_declarationsContext ctx) {
			String iName = ctx.getParent().getParent().getChild(1).getText();
			MethodInfo method = null;
			AttributeInfo attr = null;
			List<CSharp4Parser.Interface_member_declarationContext> iMemberList = ctx.interface_member_declaration();
			if(iMemberList!=null && iMemberList.size()>0)
			{
				for(CSharp4Parser.Interface_member_declarationContext iMember : iMemberList)
				{
					//if(iMember!=null && iMember.type()==null)
					if(iMember!=null)
					{
						method = new MethodInfo();
						method.setAccessModifier("public");
						method.setReturnType(iMember.getChild(0).getText());
						method.setName(iMember.getChild(1).getText());
						if(iMember.formal_parameter_list()!=null && iMember.formal_parameter_list().fixed_parameters()!=null
								&& iMember.formal_parameter_list().fixed_parameters().fixed_parameter()!=null 
								&& iMember.formal_parameter_list().fixed_parameters().fixed_parameter().size()>0) 
						{
							for(CSharp4Parser.Fixed_parameterContext fixC : iMember.formal_parameter_list().fixed_parameters().fixed_parameter())
							{
								attr = new AttributeInfo();
								
								if(fixC.type().base_type()!=null)
								{
									CSharp4Parser.Base_typeContext baseTypeContext = fixC.type().base_type();
									if(baseTypeContext!=null)
									{
										if(baseTypeContext.class_type()!=null && baseTypeContext.class_type().type_name()!=null)
										{
											CSharp4Parser.Namespace_or_type_nameContext namespaceOrTypeContext = baseTypeContext.class_type().type_name().namespace_or_type_name();
											attr.setDataType(namespaceOrTypeContext.identifier().get(0).getText());
											if(namespaceOrTypeContext.identifier().get(0).getText().equals("ICollection"))
											{
												if(namespaceOrTypeContext.type_argument_list_opt()!=null && !namespaceOrTypeContext.type_argument_list_opt().isEmpty())
												{
													if(namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments()!=null)
													{
														CSharp4Parser.Base_typeContext childBaseType = namespaceOrTypeContext.type_argument_list_opt().get(0).type_argument_list().type_arguments().type_argument().get(0).type().base_type();
														//TODO : traverse each child
														attr.setDataTypeArg(childBaseType.getText());
													}
												}
											}
										}
										else if(baseTypeContext.simple_type()!=null)
										{
											attr.setDataType(baseTypeContext.simple_type().getText());
										}
										else if(baseTypeContext.class_type()!=null)
										{
											attr.setDataType(baseTypeContext.class_type().getText());
										}
									}
								}
								if(fixC.identifier()!=null)
									attr.setName(fixC.identifier().getText());
								method.getArguments().add(attr);
							}
						}
						fileData.getInterfaceMap().get(iName).getMethods().add(method);
					}
				}
			}
		}

		@Override
		public void enterInterface_definition(CSharp4Parser.Interface_definitionContext ctx) {
			InterfaceInfo interfaceInfo = new InterfaceInfo();
			List<ClassInfo> implementers = new ArrayList<>();
			CSharp4Parser.Type_declarationContext typeDecContext = (CSharp4Parser.Type_declarationContext) ctx.getParent();
			if(typeDecContext.all_member_modifiers()!=null && typeDecContext.all_member_modifiers().all_member_modifier()!=null
					&& typeDecContext.all_member_modifiers().all_member_modifier().size()>0)
			{
				interfaceInfo.setAccessModifier(typeDecContext.all_member_modifiers().all_member_modifier().get(0).getText());
			}
			interfaceInfo.setName(ctx.identifier().getText());
			for(int i=0;i<fileData.getClassMap().size();i++)
			{
				for (String className : fileData.getClassMap().keySet()) {
					ClassInfo implementer = fileData.getClassMap().get(className);
					
					//if  interface is set as parent class
					if(implementer.getParentClass()!=null && implementer.getParentClass().getName().equals(interfaceInfo.getName()))
					{
						fileData.getClassMap().get(className).setParentClass(null);
						fileData.getClassMap().get(className).getInterfaces().add(interfaceInfo);
					}
					else
					{
						List<InterfaceInfo> iList = implementer.getInterfaces();
						for (InterfaceInfo interf : iList) {
							if(interf.getName().equals(interfaceInfo.getName()))
								implementers.add(implementer);
						}
					}
				}
			}
			interfaceInfo.setImplementers(implementers);
			fileData.getInterfaceMap().put(interfaceInfo.getName(),interfaceInfo);
		}

		
		
}
