package csse374.revengd.application;

import java.io.OutputStream;
import java.util.*;

import edu.rosehulman.jvm.sigevaluator.FieldEvaluator;
import edu.rosehulman.jvm.sigevaluator.GenericType;
import edu.rosehulman.jvm.sigevaluator.MethodEvaluator;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.tagkit.Tag;
import soot.util.Chain;

public class RelationshipFinder extends Analyzable {
	private AnalyzableData data;
	
	@Override
	public void analyze(AnalyzableData data, OutputStream out) {
		this.data = data;
		boolean recur = data.getConfigMap().containsKey("-r");
		Set<SootClass> sootClasses = data.getSootClasses();
		Collection<Relationship> relationships = new ArrayList<>();
		sootClasses.forEach(clazz -> {
			Relationship r = new Relationship(clazz);
			hasAFinder(r);
			extendsAFinder(r);
			implementsAFinder(r);
			usesAFinder(r);
			relationships.add(r);
			r.filterIn(sootClasses);
			//put IFilters in, probably in each method
		});
		data.setRelationships(relationships);
	}
	
	private void hasAFinder(Relationship r) {
		SootClass clazz = r.getThisClass();
		if(clazz.getName().equals("java.lang.Object")){
			return;
		}
		Scene scene = this.data.getScene();
		
		r.setHas(new HashMap<>());
		clazz.getFields().forEach(f -> {
			Tag signatureTag = f.getTag("SignatureTag");
			if(signatureTag != null) {
				// Use SignatureEvaluator API for parsing the field signature
				String signature = signatureTag.toString();
				FieldEvaluator fieldEvaluator = new FieldEvaluator(signature);
				GenericType fieldType = fieldEvaluator.getType();
				Set<String> elementTypes = fieldType.getAllElementTypes();
				elementTypes.forEach(element -> {
					r.addHas(scene.getSootClass(element), true);
				});
				// Add container types as well maybe
			}
			else {
				String typeString = f.getType().toString();
				if (typeString.contains("[]")){
					r.addHas(scene.getSootClass(typeString.replace("[]", "")), true);
				} else {
					r.addHas(scene.getSootClass(typeString), false);
				}
			}
		});
		
	}
	
	private void extendsAFinder(Relationship r) {
		SootClass clazz = r.getThisClass();
		if(clazz.getName().equals("java.lang.Object")){
			return;
		}
		SootClass sClazz = clazz.getSuperclass();
		r.setExtendz(sClazz);
	}
	
	public void implementsAFinder(Relationship r) {
		SootClass clazz = r.getThisClass();
		if(clazz.getName().equals("java.lang.Object")){
			return;
		}
		Chain<SootClass> iClazz = clazz.getInterfaces();
		Set<SootClass> iClazzSet = new HashSet<>();
		iClazzSet.addAll(iClazz);
		r.setImplementz(iClazzSet);		
	}
	
	public void usesAFinder(Relationship r) {
		SootClass clazz = r.getThisClass();
		if(clazz.getName().equals("java.lang.Object")){
			return;
		}
		
		Scene scene = this.data.getScene();
		
		r.setUses(new HashMap<>());
		clazz.getMethods().forEach(m -> {
			Tag signatureTag = m.getTag("SignatureTag");
			// TODO: Workaround because java.* breaks methodEvaluator
			if(signatureTag != null && !m.getDeclaringClass().toString().startsWith("java")) {
				// Use SignatureEvaluator API for parsing the field signature
				String signature = signatureTag.toString();
				MethodEvaluator methodEvaluator = new MethodEvaluator(signature);
				Set<GenericType> depends = new HashSet<>();
				depends.add(methodEvaluator.getReturnType());
				depends.addAll(methodEvaluator.getParameterTypes());
				for (GenericType depend : depends) {
					//elementTypes is empty if the GenericType is not a collection
					Set<String> elementTypes = depend.getAllElementTypes();
					if (elementTypes.isEmpty()) {
						r.addUses(scene.getSootClass(depend.getContainerType()), false);
					} else {
						elementTypes.forEach(element -> {
							r.addUses(scene.getSootClass(element), true);
						});
					}
				}
				// Add container types as well maybe
			}
			else {
				Set<Type> depends = new HashSet<>();
				depends.add(m.getReturnType());
				depends.addAll(m.getParameterTypes());
				for (Type depend : depends) {
					String typeString = depend.toString();
					if (typeString.contains("[]")){
						r.addUses(scene.getSootClass(typeString.replace("[]", "")), true);
					} else {
						r.addUses(scene.getSootClass(typeString), false);
					}
				}
				
			}
		});
	}
	
	
}
