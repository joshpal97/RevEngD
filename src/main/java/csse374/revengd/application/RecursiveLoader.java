package csse374.revengd.application;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.SootClass;

public class RecursiveLoader extends Analyzable {

	@Override
	public void analyze(AnalyzableData data, OutputStream out) {
		Map<String, String> configMap  = data.getConfigMap();
		if(!configMap.containsKey("-r")){
			return;
		}
		Set<SootClass> sootClasses = data.getSootClasses();
		Set<SootClass> temp = new HashSet<>();
		temp.addAll(sootClasses);
		
		temp.forEach(t -> {
			computeAllSuperTypes(t,sootClasses);
		});
//		System.out.println("----Recur Loaded----");
//		classNames.forEach(name -> {
//			SootClass clazz = scene.getSootClass(name);
//			sootClasses.add(clazz);
//			computeAllSuperTypes(clazz, sootClasses);
//		});
//
//		sootClasses.forEach(clazz -> {
//			System.out.println(clazz.getName());
//		});
//
//		System.out.println("-----------------------------");
//
//		return sootClasses;
	}

	void computeAllSuperTypes(final SootClass clazz, final Collection<SootClass> allSuperTypes) {
		if (clazz.getName().equals("java.lang.Object"))
			return;

		Collection<SootClass> directSuperTypes = new ArrayList<SootClass>();

		SootClass superClazz = clazz.getSuperclass();
		if (superClazz != null)
			directSuperTypes.add(superClazz);

		if (clazz.getInterfaceCount() > 0)
			directSuperTypes.addAll(clazz.getInterfaces());

		directSuperTypes.forEach(aType -> {
			if (!allSuperTypes.contains(aType)) {
				allSuperTypes.add(aType);
				this.computeAllSuperTypes(aType, allSuperTypes);
			}
		});
	}

}