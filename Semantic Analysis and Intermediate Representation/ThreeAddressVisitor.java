import java.util.*;

public class ThreeAddressVisitor implements CALParserVisitor {
	String label = "L0";
	String prevLabel;
	int curTempCount = 0;
	int paramCount = 0;
	int labelCount = 0;
	HashMap<String, ArrayList<AddressCode>> addrCode = new HashMap<>();
	HashMap<String, String> jumpLabelMap = new HashMap<>();
	
	public Object visit(SimpleNode node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	public Object visit(ASTProgram node, Object data) {
		System.out.println("***************************");
		
		System.out.println();
		
		System.out.println("**** IR using 3-address code ****");
		node.childrenAccept(this, data);
		
		Set keys = addrCode.keySet();
		if(keys.size() > 0) {
			Iterator iter = keys.iterator();
			while(iter.hasNext()) {
				String s = (String) iter.next();
				ArrayList<AddressCode> a = addrCode.get(s);
				System.out.println(s);
				for(int i=0; i<a.size(); i++) {
					System.out.println(" " + a.get(i).toString());
				}
			}
		} else {
			System.out.println("Nothing declared");
		}
		System.out.println("*********************************");
		
		return null;
	}
	
	public Object visit(ASTDeclList node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	public Object visit(ASTVar node, Object data) {
		return null;
	}
	
	// 3 children; ID, Type, Number / Boolean
	public Object visit(ASTConst node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if (allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "=";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr3 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		ac.addr4 = (String) node.jjtGetChild(2).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTID node, Object data) {
		return ((Token) node.jjtGetValue()).image;
	}
	
	public Object visit(ASTType node, Object data) {
		return ((Token) node.jjtGetValue()).image;
	}
	
	public Object visit(ASTNumber node, Object data) {
		return ((Token) node.jjtGetValue()).image;
	}
	
	public Object visit(ASTBoolean node, Object data) {
		return ((Token) node.jjtGetValue()).image;
	}
	
	public Object visit(ASTFunctionList node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	// 4 children; Type, ID, ParameterList, FunctionBody
	public Object visit(ASTFunctionDecl node, Object data) {
		prevLabel = label;
		label = "L" + (labelCount + 1);
		
		jumpLabelMap.put((String) node.jjtGetChild(1).jjtAccept(this, null), label);
		
		node.childrenAccept(this, data);
		
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ret = new AddressCode();
		ret.addr1 = "return";
		
		allAc.add(ret);
		addrCode.put(label, allAc);
		
		label = prevLabel;
		labelCount++;
		
		return null;
	}
	
	public Object visit(ASTReturn node, Object data) {
		return null;
	}
	
	public Object visit(ASTParameterList node, Object data) {
		return null;
	}
	
	public Object visit(ASTParam node, Object data) {
		return null;
	}
	
	public Object visit(ASTFunctionBody node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	// 2 children; DeclList, StatementList
	public Object visit(ASTMain node, Object data) {
		prevLabel = label;
		label = "L" + (labelCount + 1);
		
		node.childrenAccept(this, data);
		
		label = prevLabel;
		labelCount++;
		
		return null;
	}
	
	public Object visit(ASTStatementList node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	public Object visit(ASTAssignment node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "=";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr3 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTFunctionCall node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac1 = new AddressCode();
		ac1.addr1 = "funcCall";
		ac1.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		if(node.jjtGetNumChildren() > 1) {
			ac1.addr3 = (String) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtAccept(this, null);
		}
		allAc.add(ac1);
		
		AddressCode ac2 = new AddressCode();
		ac2.addr1 = "goto";
		ac2.addr2 = jumpLabelMap.get((String) node.jjtGetChild(0).jjtAccept(this,null));
		
		allAc.add(ac2);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTArgList node, Object data) {
		return null;
	}
	
	public Object visit(ASTArg node, Object data) {
		/*
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "param";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		*/
		return null;
	}
	
	public Object visit(ASTAND node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "&&";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTOR node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "||";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTLessThan node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "<";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTLessThanOrEqualTo node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "<=";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTGreaterThan node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = ">";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTGreaterThanOrEqualTo node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = ">=";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTEquals node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "==";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTNotEquals node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "!=";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTNot node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "~";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTNegative node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "-";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTPositive node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "+";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTAdd node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "+";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTSubtract node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "-";
		ac.addr2 = (String) node.jjtGetChild(0).jjtAccept(this, null);
		ac.addr2 = (String) node.jjtGetChild(1).jjtAccept(this, null);
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTSkip node, Object data) {
		ArrayList<AddressCode> allAc = addrCode.get(label);
		if(allAc == null) {
			allAc = new ArrayList<>();
		}
		
		AddressCode ac = new AddressCode();
		ac.addr1 = "Skip";
		
		allAc.add(ac);
		addrCode.put(label, allAc);
		
		return null;
	}
	
	public Object visit(ASTIf node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	public Object visit(ASTWhile node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
	
	public Object visit(ASTCondition node, Object data) {
		node.childrenAccept(this, data);
		return null;
	}
}
