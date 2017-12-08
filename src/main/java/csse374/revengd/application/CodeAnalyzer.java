package csse374.revengd.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import csse374.revengd.soot.SceneBuilder;
import soot.Scene;
import soot.SootClass;

public class CodeAnalyzer {
	private Set<SootClass> sootClasses;
	private Analyzable analyzable;
	private String path;
	private List<String> classNames;
	
	public CodeAnalyzer() {
		this.classNames = new ArrayList<>();
		this.analyzable = new RegularAnalyzable(); // default analyzable
	}
	
	public void analyze(){
		this.sootClasses = this.analyzable.analyze(this.path, this.classNames);
		
	}
	
	public void addClassName(String className) {
		this.classNames.add(className);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setAnalyzable(Analyzable analyzable) {
		this.analyzable = analyzable;
	}
	
	public Set<SootClass> getSootClasses() {
		return this.sootClasses;
	}
}
