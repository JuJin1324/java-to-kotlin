import static java.util.Comparator.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 4/6/24
 */
public class Main {

	public static void main(String[] args) {
		var members = new ArrayList<Member>();
		members.add(new Member("멤버1"));
		members.add(new Member("멤버2"));
		members.add(new Member("멤버3"));

		var chatRoom = new ChatRoom(members);
		System.out.println("chatRoom.getMembers() = " + chatRoom.getMembers());
		var firstSorted = getFirstSorted(chatRoom.getMembers());
		System.out.println("firstSorted = " + firstSorted);
		System.out.println("chatRoom.getMembers() = " + chatRoom.getMembers());
	}

	private static Member getFirstSorted(List<Member> members) {
		// 파라미터로 받은 members 내의 정보를 조작한다.
		// List.sort 는 원본에 해당하는 리스트를 정렬한다.
		// 이는 위의 ChatRoom 객체 내의 members 정보도 같이 조작이된 상태로 변한 것을 의미한다.
		members.sort(comparing(Member::getName).reversed());
		return members.get(0);
	}
}
