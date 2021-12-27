package demo.aws.modules.dynamo;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class GameCharacter {
	
	private String region;
	
	private String characterName;
	
	private String race;
	
	private String profession;
	
	private String version;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("Region")
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@DynamoDbSortKey
	@DynamoDbAttribute("CharacterName")
	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	@DynamoDbAttribute("Race")
	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	@DynamoDbAttribute("Profession")
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@DynamoDbAttribute("Version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return String.format("Character [region=%s, characterName=%s, race=%s, profession=%s, version=%s]", region,
				characterName, race, profession, version);
	}

}
