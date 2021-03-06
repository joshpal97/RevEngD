package csse374.revengd.application;

import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import soot.SootClass;

public class SingletonDetector extends Analyzable {
	public static final String PATTERN = "singleton";
	public static final String SINGLETON = "singleton";

	@Override
	public void analyze(AnalyzableData data, OutputStream out) {
		this.addAllAvailableFilters();
		Collection<Relationship> relationships = data.getRelationships();
		relationships.forEach(r -> {
			if(this.useFiltersOn(r.getThisClass()) && isSingleton(r)){
				IPattern pattern = new Pattern(PATTERN);
				pattern.putComponent(SINGLETON, r);
				data.putPattern(PATTERN, pattern);
			}
		});
		
	}

	private static boolean isSingleton(Relationship r) {
		Set<SootClass> supers = new HashSet<>();
		supers.add(r.getThisClass());
		supers.add(r.getExtendz());
		supers.addAll(r.getImplementz());
		supers.remove(null);
		 return  hasANonPublicStaticSupertype(r, supers)
				 && publicStaticGetters(r, supers)
				 && privateConstructors(r.getThisClass());
	}
	
	private static boolean hasANonPublicStaticSupertype(Relationship r, Set<SootClass> supers) {
		Set<String> stringSupers = supers.stream()
				.map(clazz -> {
					return clazz.toString();
					})
				.collect(Collectors.toSet());

		return supers.stream().anyMatch(clazz -> {
					return r.has(clazz) && ! r.hasMany(clazz);
				})
				&& r.getThisClass().getFields().stream().allMatch(f -> {
					return !(stringSupers.contains(f.getType().toString()))
							|| (f.isStatic() && !f.isPublic());
				});
	}
	
	private static boolean publicStaticGetters(Relationship r, Set<SootClass> supers) {
		Set<String> stringSupers = supers.stream()
				.map(clazz -> {
					return clazz.toString();})
				.collect(Collectors.toSet());
		return r.getThisClass().getMethods().stream().anyMatch(m -> {
			 return m.isStatic()
					 && m.isPublic()
					 && stringSupers.contains(m.getReturnType().toString());
		 });
	}
	
	private static boolean privateConstructors(SootClass clazz) {
		return clazz.getMethods().stream().allMatch(m -> {
			 return !m.isConstructor()
					 || !m.isPublic();
		 });
	}
}
