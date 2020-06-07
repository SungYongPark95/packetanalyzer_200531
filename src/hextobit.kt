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
    var hexT = hexTest.substring(28)


    print("1.Ethernet\n\t1) Destination Address : ")
    addrView(ethDestAddr)

    print("\t2) Source Address : ")
    addrView(ethSourAddr)

    print("\t3) Type : " + ethType)
    if (ethType.equals("0800")){
        print(" / IP\n" +
                "2.IP\n" +
                "\t1) Version : 04\n" +
                "\t2) Header Length : ")
        //IPv4 Header Length = min 20 to max 60, take string and convert to byte
        var hexTemp = slicePair(hexT, 1, 2)
//        val ipHeaderLeng = hexT.substring(1,2)
//        hexT = hexT.substring(2)
        var ipheaderLeng = ipv4headerLeng(hexTemp.first)
        hexTemp = slicePair(hexTemp.second, 0,2)
        print("\t3) Service Type : "+ hexTemp.first)
        if(hexTemp.first == "00"){
            println(" / No service type")
        }else {
            print(" / DS : ")
            ipv4ToS(hexTemp.first)
        }
        hexTemp = slicePair(hexTemp.second, 0, 4)
        var ipv4TotalLeng = hexTemp.first.toInt(radix = 16)
        var ipv4Payload = ipv4TotalLeng - ipheaderLeng
        print("\t4) Total Length : "+ hexTemp.first + " / " + ipv4TotalLeng + " bytes : " + ipv4Payload + " bytes payload")




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

fun ipv4headerLeng(leng : String) : Int {
    var intHeadLeng = leng.toInt() * 4
    if (intHeadLeng == 20){
        println(leng + " / 20 byte : No-option")
    }else if(intHeadLeng > 60){
        println(leng + " / More than maximum value")
    }
    else{
        println(leng + " / " + intHeadLeng + " byte : Options exist")
    }
    return intHeadLeng
}

fun slicePair(t : String, i : Int, j : Int) : Pair<String, String>{
    val a = t.substring(i, j)
    val b = t.substring(j)
    return Pair(a,b)
}

fun ipv4ToS(t : String) {
    var pairHexTos = slicePair(t, 0,1)
    var temp2 = pairHexTos.second.toInt(radix = 16)
    var temp3 = Integer.toString(temp2,2)
    var temp4 = String.format("%4s", temp3)
    temp4 = temp4.replace(" ", "0")
    var pairBinaryTos = slicePair(temp4, 0,2)
    if(pairBinaryTos.first == "00"){
        print("Normal")
    }else if (pairBinaryTos.first == "11"){
        print("experimental/local")
    }else{
        print("experimental/booking")
    }
    print(" / ECN : ")
    if(pairBinaryTos.second == "00"){
        println("Didn't use ECN")
    }else if (pairBinaryTos.second == "11"){
        println("Router congestion")
    }else{
        println("Endpoint accepts the ECN")
    }
}