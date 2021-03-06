package csse374.revengd.application;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import soot.Scene;
import soot.SootClass;


public class AnalyzableData {
	private Map <String, String> configMap;
	private Scene scene;
	private Set <SootClass> sootClasses;
	private Collection <Relationship> relationships;
	private String umlText;
	private SetMultimap<String, IPattern> patterns;
	
	public AnalyzableData(Map<String, String> argMap) {
		this.configMap = argMap;
		this.patterns = HashMultimap.create();
	}
	public Map<String, String> getConfigMap() {
		return this.configMap;
	}
	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}
	public Scene getScene() {
		return this.scene;
	}
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	public Set<SootClass> getSootClasses() {
		return this.sootClasses;
	}
	public void setSootClasses(Set<SootClass> sootClasses) {
		this.sootClasses = sootClasses;
	}
	public Collection<Relationship> getRelationships() {
		return this.relationships;
	}
	public void setRelationships(Collection<Relationship> relationships) {
		this.relationships = relationships;
	}
	public String getUmlText() {
		return this.umlText;
	}
	public void setUmlText(String umlText) {
		this.umlText = umlText;
	}
	public Set<IPattern> getPatternsByName(String name){
		if (this.patterns.containsKey(name)) {
			return this.patterns.get(name);
		}
		return new HashSet<>();
	}
	public void putPattern(String name, IPattern p){
		this.patterns.put(name, p);
	}
	public Relationship getRelationship(SootClass clazz){
		if (clazz == null) {
			return null;
		}
		for(Relationship r : this.relationships){
			if(r.getThisClass().equals(clazz)){
				return r;
			}
			
		}
		return new Relationship(clazz);
	}

	
}
