package spinal.lib.bus.regif

import spinal.core.SpinalError

import scala.collection.mutable.ListBuffer


case class RegSliceGrp(baseAddr: BigInt, maxSize: BigInt, doc: String, grp: GrpTag)(bi: BusIf) {
  private val slices = ListBuffer[RegSlice]()
  def len: Int = slices.length

  def check(offset: BigInt) = {
    if (offset < 0 || offset >= maxSize) {
      SpinalError(s"Offset $offset is out of range [0, $maxSize)")
    }
  }

  def newReg(doc: String)(implicit symbol: SymbolName): RegInst = {
    val ret = bi.newReg(doc, grp)(symbol)
    slices += ret
    ret
  }

  def newRegAt(offset: Int, doc: String)(implicit symbol: SymbolName): RegInst = {
    check(offset)
    val ret = bi.newRegAt(baseAddr + offset, doc, grp)(symbol)
    slices += ret
    ret
  }

  def newFifo(doc: String)(implicit symbol: SymbolName): FifoInst = {
    val ret = bi.newFifo(doc, grp)(symbol)
    slices += ret
    ret
  }

  def newFifoAt(offset: Int, doc: String)(implicit symbol: SymbolName) = {
    check(offset)
    val ret = bi.newFifoAt(baseAddr + offset, doc, grp)(symbol)
    slices += ret
    ret
  }

  def newRAM(size: BigInt, doc: String)(implicit symbol: SymbolName) = {
    val ret = bi.newRAM(size, doc, grp)(symbol)
    slices += ret
    ret
  }

  def newRAMAt(offset: Int, size: BigInt, doc: String)(implicit symbol: SymbolName) = {
    check(offset + size)
    val ret = bi.newRAMAt(baseAddr + offset, size, doc, grp)(symbol)
    slices += ret
    ret
  }
}
