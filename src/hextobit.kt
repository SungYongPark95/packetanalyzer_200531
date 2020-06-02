import java.lang.StringBuilder

fun main() {
    //1.Get the packet from user
    println("Input the packet!")
    var hexTest:String = readLine()!!

    //2. Designate each range for slicing (Ethernet Part)
    var lengEthDestAddr = IntRange(0,11)
    var lengEthSourAddr = IntRange(12,23)
//    var lengEthAddr = 12
    var lengEthType = IntRange(24,27)

    //3. Slice the each part at Ethernet
    var ethDestAddr = hexTest.slice(lengEthDestAddr)
    var ethSourAddr = hexTest.slice(lengEthSourAddr)
//    println(hexTest.slice(lengEthAddr))
    var ethType = hexTest.slice(lengEthType)


    print("1) Destination Address : ")
    var builder : StringBuilder? = StringBuilder()
    var buildCast : StringBuilder? = StringBuilder()
    builder?.append(ethDestAddr)
    for(i in 2..15 step 3)
    builder?.insert(i,":")
    print(builder)
//  if (builder.toString())
    println(castUni(builder.toString().substring(1,2)))

    print("2) Source Address : ")
    builder?.setLength(0);
    builder?.append(ethSourAddr)
    for(i in 2..15 step 3)
        builder?.insert(i,":")
    print(builder)
    println(castUni(builder.toString().substring(1,2)))
    

    if (ethType.equals("0800")){
    }
}

fun castUni(i :String): String {
    var a = i.toInt(16)
    if (a % 2 ==0)
        return " / Unicast"
    else
        return " / Multicast"
}