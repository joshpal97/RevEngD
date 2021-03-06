package csse374.revengd.application;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import csse374.revengd.soot.MainMethodMatcher;
import csse374.revengd.soot.SceneBuilder;
import soot.Scene;
import soot.SootClass;

public class SootLoader extends Analyzable {

	@Override
	public void analyze(AnalyzableData data, OutputStream out) {
		Map<String, String> configMap  = data.getConfigMap();
		String path = configMap.get("path");
		String classNamesString = configMap.get("class");
		String [] classNames = classNamesString.trim().split(" ");
		String [] pathNames;
		if (path.contains(",")) {
			pathNames = path.trim().split(",");
		} else {
			pathNames = path.trim().split(" ");
		}
		Scene scene = SceneBuilder.create()
				.addDirectories(Arrays.asList(pathNames))
				.addClasses(Arrays.asList(classNames))
				.setEntryClass(classNames[0])
				.addEntryPointMatcher(new MainMethodMatcher(classNames[0]))
				.build();
		
		Set<SootClass> sootClasses = new HashSet<>();

		System.out.println("----Loaded----");
		for(int i = 0; i < classNames.length; i++) {
			SootClass clazz = scene.getSootClass(classNames[i]);
			boolean keep = this.useFiltersOn(clazz);
			
			if(keep) {
				sootClasses.add(clazz);
				System.out.println(clazz.getName());
			}
		}
		System.out.println("-----------------------------");
		data.setSootClasses(sootClasses);
		data.setScene(scene);

	}

}
