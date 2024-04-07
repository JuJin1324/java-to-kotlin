import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 4/6/24
 */
public class ChatRoom {
	private final List<Member> members;

	public ChatRoom(List<Member> members) {
		this.members = new ArrayList<>(members);
	}

	public List<Member> getMembers() {
		return Collections.unmodifiableList(this.members);
	}
}
