package csse374.revengd.application;

import soot.SootClass;

public class UsesRelatable implements Relatable{

	@Override
	public void findRelationships(Relationship r) {
		SootClass clazz = r.getThisClass();
		if(clazz.getName().equals(Relatable.OBJECT)){
			return;
		}
	}

}
