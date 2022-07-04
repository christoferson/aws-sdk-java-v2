package demo.aws.modules.dynamo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamoAttributeList {
	
	private List<DynamoAttribute> list = new ArrayList<>();
	
	public static DynamoAttributeList newInstance() {
		return new DynamoAttributeList();
	}
	
	public DynamoAttributeList put(DynamoAttribute attribute) {
		this.list.add(attribute);
		return this;
	}
	
	public List<DynamoAttribute> items() {
		return Collections.unmodifiableList(list);
	}

}
