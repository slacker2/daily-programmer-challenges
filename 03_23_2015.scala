// http://www.reddit.com/r/dailyprogrammer/comments/2zyipu/20150323_challenge_207_easy_bioinformatics_1_dna/

object DPG03232015 {

  def main(args: Array[String]): Unit = {

    println(args(0))
    originalChallenge(args)
    println
    extraCredit(args)
    println
  }

  def originalChallenge(args: Array[String]) = {

    args(0).split(" ").map { p =>
      p match {
        case "A" => print("T ")
        case "T" => print("A ")
        case "G" => print("C ")
        case "C" => print("G ")
      }
    }
    println

  }

  def extraCredit(args: Array[String]) = {

    args(0).split(" ").grouped(3).foreach {
      _.fold(""){(x,v)=>x+v} match {
        case "TTT" | "TTC" => print("Phe ")
        case "TTA" | "TTG" | "CTT" | "CTC" | "CTA" | "CTG" => print("Leu ")
        case "ATT" | "ATC" | "ATA" => print("Ile ")
        case "ATG" => print("Met ")
        case "GTT" | "GTC" | "GTA" | "GTG" | "GCT" | "GCC" | "GCA" | "GCG" => print("Ala ")
        case "TCT" | "TCC" | "TCA" | "TCG" | "AGT" | "AGC" => print("Ser ")
        case "CCT" | "CCC" | "CCA" | "CCG" => print("Pro ")
        case "ACT" | "ACC" | "ACA" | "ACG" => print("Thr ")
        case "TAT" | "TAC" => print("Tyr ")
        case "CAT" | "CAC" => print("His ")
        case "CAA" | "CAG" => print("Gln ")
        case "AAT" | "AAC" => print("Asn ")
        case "AAA" | "AAG" => print("Lys ")
        case "GAT" | "GAC" => print("Asp ")
        case "GAA" | "GAG" => print("Glu ")
        case "TGT" | "TGC" => print("Cys ")
        case "TGA" | "TAA" | "TAG" => print("STOP ")
        case "TGG" => print("Trp ")
        case "CGT" | "CGC" | "CGA" | "CGG" | "AGA" | "AGG" => print("Arg ")
        case "GGT" | "GGC" | "GGA" | "GGG" => print("Gly ")
      }
    }
  }

}
