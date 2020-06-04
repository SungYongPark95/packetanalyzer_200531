import java.lang.StringBuilder

fun main() {
    //1.Get the packet from user
    println("Input the packet!")
    var hexTest:String = readLine()!!

    //2. Designate each range for slicing (Ethernet Part)
    val lengEthDestAddr = IntRange(0,11)
    val lengEthSourAddr = IntRange(12,23)
//    var lengEthAddr = 12
    val lengEthType = IntRange(24,27)

    //3. Slice the each part at Ethernet
    val ethDestAddr = hexTest.slice(lengEthDestAddr)
    val ethSourAddr = hexTest.slice(lengEthSourAddr)
//    println(hexTest.slice(lengEthAddr))
    val ethType = hexTest.slice(lengEthType)
    hexTest = hexTest.substring(28)


    print("1.Ethernet\n\t1) Destination Address : ")
    addrView(ethDestAddr)

    print("\t2) Source Address : ")
    addrView(ethSourAddr)

    print("\t3) Type : " + ethType)
    if (ethType.equals("0800")){
        println(" / IP\n" +
                "2.IP\n" +
                "\t1) Version : 04\n" +
                "\t2) Header Length : ")


    }else if(ethType.equals("0806")){
        println(" / ARP\n" +
                "2.ARP")
    }
    else if(ethType.equals("86dd")){
        println(" / IPv6\n" +
                "2.IPv6\n" +
                "\t1) Version : 06")
    }else{
        println(" / Ethernet or Other kinds of Protocol")
    }
}

fun castUni(i :String): String {
    val a = i.substring(1,2).toInt(16)
    if (i.equals("ff:ff:ff:ff:ff:ff"))
        return " / Broadcast"
    if (a % 2 == 0)
        return " / Unicast"
    else
        return " / Multicast"
}

fun addrView(addr : String){
    var builder : StringBuilder? = StringBuilder()
    builder?.append(addr)
    for(i in 2..15 step 3)
        builder?.insert(i,":")
    println(builder?.append(castUni(builder.toString())))
}