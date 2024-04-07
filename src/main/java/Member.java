/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 4/6/24
 */

public class Member {
	private final String name;

	public Member(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Member{" +
			"name='" + name + '\'' +
			'}';
	}
}
