package practice.sample

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 4/6/24
 */
fun main() {
    val members = mutableListOf(
        Member("멤버1"),
        Member("멤버2"),
        Member("멤버3")
    )

    val chatRoom = ChatRoom(members)
    println("chatRoom.members = ${chatRoom.members}")
    val firstSorted = chatRoom.members.getFirstSorted()
    println("firstSorted = ${firstSorted}")
    println("chatRoom.members = ${chatRoom.members}")
}

private fun List<Member>.getFirstSorted(): Member = minBy { it.name }
