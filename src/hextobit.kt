import java.lang.String.format
import java.lang.StringBuilder
import kotlin.system.exitProcess

// 과정중에 16진수로 제대로 변환하지 않은 곳이 있는지 확인할 것
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
        val ipheaderLeng = ipv4headerLeng(hexTemp.first)
        hexTemp = slicePair(hexTemp.second, 0,2)
        print("\t3) Service Type : "+ hexTemp.first)
        if(hexTemp.first == "00"){
            println(" / No service type")
        }else {
            print(" / DS : ")
            ipv4ToS(hexTemp.first)
        }
        hexTemp = slicePair(hexTemp.second, 0, 4)
        val ipv4TotalLeng = hexTemp.first.toInt(radix = 16)
        val ipv4Payload = ipv4TotalLeng - ipheaderLeng
        println("\t4) Total Length : "+ hexTemp.first + " / " + ipv4TotalLeng + " bytes : " + ipv4Payload + " bytes payload")

        hexTemp = slicePair(hexTemp.second, 0, 4)
        val ipv4Iden = hexTemp.first.toInt(radix = 16)
        print("\t5) Identification : "+ hexTemp.first+ " / " + ipv4Iden + " /")
        ipv4IdenSlice(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 1)
        print("\t6) Flags : " + hexTemp.first)
        ipv4Flags(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 3)
        ipv4Offset(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0,2)
        ipv4TTL(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0,2)
        val ipv4ptc = ipv4Protocol(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0,4)
        val ipv4checksum = hexTemp.first
        println("\t10) Checksum : " + ipv4checksum)

        hexTemp = slicePair(hexTemp.second, 0,8)
        print("\t11) Source Address : ")
        ipv4addr(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0,8)
        print("\t12) Destination Address : ")
        ipv4addr(hexTemp.first)

        if(ipv4ptc == 1)
        {
            println("3. ICMP")
            hexTemp = slicePair(hexTemp.second, 0,2)
            print("\t1) Type : " + hexTemp.first + " / ")
            val icmpType = icmpTP(hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0,2)
            icmpCode(hexTemp.first, icmpType)

            hexTemp = slicePair(hexTemp.second, 0,4)
            val icmpChecksum = hexTemp.first
            println("\t3) Checksum : " + icmpChecksum)

            println("\t3) Rest Data : " + hexTemp.second  + " / " + (hexTemp.second.length/2) + " bytes")

        }else if (ipv4ptc == 2 ){
            println("3. IGMP")
        }else if (ipv4ptc == 6 ){
            println("3. TCP")

            hexTemp = slicePair(hexTemp.second, 0, 4)
            print("\t1) Source Port : ")
            val tcpSourcePort = tcpPort(hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0, 4)
            print("\t2) Destination Port : ")
            val tcpDestPort = tcpPort(hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0, 8)
            println("\t3) Sequence number : " + hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0, 8)
            println("\t4) Ack number : " + hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0, 1)
            var tcpHeaderLeng = tcpHeaderLeng(hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 1, 3)
            tcpControlBit(hexTemp.first)

            hexTemp = slicePair(hexTemp.second, 0, 4)
            val tcpWindow = hexTemp.first.toInt(16)
            println("\t7) Window Size : " + hexTemp.first + " / " + tcpWindow + " bytes")

            hexTemp = slicePair(hexTemp.second, 0, 4)
            val tcpChecksum = hexTemp.first
            println("\t8) Checksum : " + tcpChecksum)

            hexTemp = slicePair(hexTemp.second, 0, 4)
            val tcpUrgPoint = hexTemp.first
            print("\t9) Urgent Point : ")
            if(tcpUrgPoint == "0000"){
                println("0000 / Not Urgent")
            }else{
                println(tcpUrgPoint + " / Check the Urgent Point")
            }

            val tcpOpt = hexTemp.second
            if(tcpHeaderLeng == 20){

            }else{
                println("\t10) Option : " + tcpOpt + " / " + (tcpOpt.length/2) + " bytes")
            }

        }else if (ipv4ptc == 8 ){
            println("3. EGP")
        }else if (ipv4ptc == 17 ){
            println("3. UDP")
        }else if (ipv4ptc == 89 ){
            println("3. OSPF")
        }



    }else if(ethType.equals("0806")){
        println(" / ARP\n" +
                "2.ARP")
        var hexTemp = slicePair(hexT, 0, 4)
        val arpHWType = arpHWT(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 4)
        val arpProtocolType = arpPTT(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 2)
        val arpHWSize = arpHWS(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 2)
        val arpProtocolSize = hexTemp.first.toInt(16) * 8
        println("\t4) Protocol Size : "+ hexTemp.first + " / " + arpProtocolSize + " bits")

        hexTemp = slicePair(hexTemp.second, 0, 4)
        val arpOperation = hexTemp.first
        arpOper(arpOperation)

        hexTemp = slicePair(hexTemp.second, 0, 12)
        print("\t6) Sender MAC Address : ")
        addrView(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 8)
        print("\t7) Sender IP Address : ")
        ipv4addr(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 12)
        print("\t8) Target MAC Address : ")
        addrView(hexTemp.first)

        hexTemp = slicePair(hexTemp.second, 0, 8)
        print("\t9) Target IP Address : ")
        ipv4addr(hexTemp.first)

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
    if (i.equals("00:00:00:00:00:00"))
        return " / Unknown MAC"
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
    var intHeadLeng = leng.toInt(16) * 4
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

fun ipv4IdenSlice (t : String) {
    for(i in 0..t.length-1){
        print(" " + t[i].toString().toInt(radix = 16))
    }
    println()
}

fun ipv4Flags (t : String){
    var temp1 = format("%4s", t.toInt(16).toString(2)).replace(" ", "0")
    println(" / " + temp1)
    println("\t\t- Reserve : " + temp1[0])
    print("\t\t- Don't Fragment : " + temp1[1] )
    if(temp1[1] == '0'){
        println(" / Able to fragment")
    }else{
        println(" / Unable to fragment")
    }
    print("\t\t- More : " + temp1[2])
    if(temp1[2] == '0'){
        println(" / No more fragments")
    }else{
        println(" / More fragments")
    }
}

fun ipv4Offset (t: String){
    print("\t7) Offset : " + t + " / ")
    if(t == "000"){
        println("First Fragment")
    }else{
        println(t + "th Fragment")
    }
}

fun ipv4TTL (t: String){
    println("\t8) TTL : " + t + " / " + t.toInt(16) + " hops")
}

fun ipv4Protocol (t: String) : Int {
    var ptc = t.toInt(16)
    print("\t9) Protocol : " + ptc + " / " )
    if(ptc == 1)
    {
        println("ICMP")
    }else if (ptc == 2 ){
        println("IGMP")
    }else if (ptc == 6 ){
        println("TCP")
    }else if (ptc == 8 ){
        println("EGP")
    }else if (ptc == 17 ){
        println("UDP")
    }else if (ptc == 89 ){
        println("OSPF")
    }
    return ptc
}

fun ipv4addr (t: String){
    print(t + " / ")
    var temp = slicePair(t, 0, 2)
    for(i in 1..4){
        print(temp.first.toInt(16))
        if (i == 4){
            println()
            return
        }
        print(".")
        temp = slicePair(temp.second, 0, 2)
    }
}

fun tcpPort (t: String) : Int{
    val tempPort = t.toInt(16)
    print( t + " / " + tempPort)
    if(tempPort in 0..1023){
        print("(Well-Known Port) : ")
        if (tempPort == 67 || tempPort == 68){
            println("DHCP")
        }else if(tempPort == 53){
            println("DNS")
        }else if(tempPort == 20){
            println("FTP (Control)")
        }else if(tempPort == 1){
            println("FTP (data)")
        }else if(tempPort == 80){
            println("WWW (HTTP)")
        }else if(tempPort == 443){
            println("WWW (HTTPS)")
        }else if(tempPort == 110){
            println("POP3")
        }else if(tempPort in 137..139){
            println("SMB")
        }else if(tempPort == 25){
            println("SMTP")
        }else if(tempPort == 22){
            println("SSH")
        }else if(tempPort == 23) {
            println("Telnet")
        }
    }else if (tempPort in 1024..49151){
        println(" : Registered Port")
    }else if (tempPort in 49152..65535){
        println(" : Dynamic and/or Private Port")
    }else{
        println(" : Unregistered Port, Please Check")
    }
    return tempPort
}

fun tcpHeaderLeng(t: String) : Int{
    var temp = t.toInt(16)
    println("\t5) Header Length : " + temp + " / " + temp * 4 + " bytes : option " + (temp * 4 - 20) + " bytes")
    return temp * 4
}

fun tcpControlBit(t: String){
    var splitTemp = slicePair(t, 0, 1)
    var sa = format("%4s", splitTemp.first.toInt(16).toString(2)).replace(" ", "0")
    var sb = format("%4s", splitTemp.second.toInt(16).toString(2)).replace(" ", "0")
    println("\t6) Control Bits : " + t + " / "+ sa + " " + sb)

    splitTemp = slicePair(sa, 2,3)
    print("\t\t- Urgent : " + splitTemp.first + " / ")
    if (splitTemp.first == "0"){
        println("Not urgent")
    } else{
        println("Urgent")
    }

    val ack = splitTemp.second
    print("\t\t- AcK : " + ack + " / ")
    if (ack == "1"){
        println("Acknowledgement")
    } else{
        println("Non-Acknowledgement")
    }

    splitTemp = slicePair(sb, 0,1)
    print("\t\t- Push : " + splitTemp.first + " / ")
    if (splitTemp.first == "0"){
        println("Normal")
    } else{
        println("ASAP or No more data")
    }

    splitTemp = slicePair(splitTemp.second, 0,1)
    print("\t\t- Reset : " + splitTemp.first + " / ")
    if (splitTemp.first == "0"){
        println("Normal")
    } else{
        println("Forced Reset or Connection Problem")
    }

    splitTemp = slicePair(splitTemp.second, 0,1)
    val syn = splitTemp.first
    print("\t\t- Syn : " + syn + " / ")
    if (ack == "0" && syn == "1"){
        println("Connection Request")
    }else if(ack == "1" && syn == "1"){
        println("Connection Approve")
    }else if(ack == "1"){
        println("Connection Setup")
    }

    val fin = splitTemp.second
    print("\t\t- Fin : " + fin + " / ")
    if (fin == "0"){
        println("Not Connection Release")
    }else if(fin == "1" && ack == "1"){
        println("Connection Closing Response")
    }else if(fin == "1"){
        println("Connection Request")
    }
}

fun arpHWT(t: String) : Int{
    val arpHWt = t.toInt(16)
    print("\t1) H/W Type : " + t + " / ")
    if (arpHWt == 1){
        println("Ethernet(10Mb)")
    }else if (arpHWt == 2){
        println("Experimental Ethernet(3Mb)")
    }else if (arpHWt == 3){
        println("Amateur Radio AX.25")
    }else if (arpHWt == 4){
        println("Proteon ProNET Token Ring")
    }else if (arpHWt == 5){
        println("Chaos")
    }else if (arpHWt == 6){
        println("IEEE 802.3 networks")
    }else if (arpHWt == 7){
        println("ARCNET")
    }else if (arpHWt == 8){
        println("Hyperchannel")
    }else if (arpHWt == 9){
        println("Lanstar")
    }else if (arpHWt == 10){
        println("Autonet Short Address")
    }else if (arpHWt == 11){
        println("LocalTalk")
    }else if (arpHWt == 12){
        println("LocalNet (IBM PCNet or SYTEK LocalNet")
    }
    return arpHWt
}

fun arpHWS(t: String) : Int{
    val arpHWs = t.toInt(16) * 8
    println("\t3) H/W Size : " + t + " / " + arpHWs + "bits")
    return arpHWs
}

fun arpPTT(t: String){
    print("\t2) Protocol Type : " + t + " / ")
    if (t == "0800"){
        println("IPv4")
    }else if (t == "0806"){
        println("ARP")
    }else if (t == "0835"){
        println("RARP")
    }else if (t == "86DD"){
        println("IPv6")
    }else if (t == "8100"){
        println("VLAN ID")
    }else if (t == "8863"){
        println("PPPoE Discovery Stage")
    }else if (t == "8864h"){
        println("PPPoE PPP Session Stage")
    }
}

fun arpOper(t: String){
    print("\t5) Operation : " + t + " / ")
    val oper = t.toInt(16)
    if (oper == 1){
        println("ARP Request")
    }else if (oper == 2){
        println("ARP Reply")
    }else if (oper == 3){
        println("RARP Request")
    }else if (oper == 4){
        println("RARP Reply")
    }
}

fun icmpTP(t: String) : Int{
    val type = t.toInt(16)
    if (type == 0){
        print("Info Message / ")
        println("Echo Reply")
    } else if (type == 8){
        print("Info Message / ")
        println("Echo Request")
    } else if (type == 9){
        print("Info Message / ")
        println("Router Advertisement")
    } else if (type == 10){
        print("Info Message / ")
        println("Router Solicitation")
    }else if (type == 3){
        print("Error Reporting Message / ")
        println("Destination Unreachable")
    }else if (type == 4){
        print("Error Reporting Message / ")
        println("Source Quench")
    }else if (type == 5){
        print("Error Reporting Message / ")
        println("Redirect")
    }else if (type == 11){
        print("Error Reporting Message / ")
        println("Time Exceeded")
    }else if (type == 12){
        print("Error Reporting Message / ")
        println("Parameter Problem")
    }
    return type
}

fun icmpCode(t: String, icmpType : Int){
    print("\t2) Code : " + t)
    val code = t.toInt(16)
    if (icmpType == 3){
        print(" / ")
        if(code == 0){
            println("Network Unreachable")
        } else if(code == 1){
            println("Host Unreachable")
        } else if(code == 2){
            println("Protocol Unreachable")
        } else if(code == 3){
            println("Port Unreachable")
        }
    }else{
        println()
    }
}
