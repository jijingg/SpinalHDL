package spinal.lib.bus.regif

import spinal.lib._
import TableTreeNodeImplicits._

final case class DocHtml(name : String) extends BusIfDoc {
  override val suffix: String = "html"
  val title : String = s"${name} Register Interface"

  override def body(): String = {
    val table = SlicesToHtmlSliceGrp(bi.slices).map(_.tbody).mkString("\n")
    HtmlTemplate.getHTML(title, table)
  }
}

trait TableTreeNode {
  val name: String
  val children: List[TableTreeNode]

  def td: String = {
    s"""<td align="center" rowspan="${span}">${name}</td>""".stripMargin
  }

  def span: Int = if (children.isEmpty) { 1 } else { children.map(_.span).sum }

  def tr_begin = """<tr align="left">""".stripMargin
  def tr: String = {
    val self = if (children.isEmpty) s"${td}</tr>" else s"${td}"
    self + children.zipWithIndex.flatMap { case (node, idx) =>
      idx match {
        case 0 => node.tr
        case _ => s"${node.tr_begin}${node.tr}"
      }
    }.mkString("")
  }

  def tbody = s"""$tr_begin $tr"""

  def br(s: String) = s.replaceAll("\\\\n|\n", "<br>")
}

case class HtmlSliceGrp(name: String, children: List[RegSlice])

object SlicesToHtmlSliceGrp {
  def apply(slices: List[RegSlice]): List[HtmlSliceGrp] = {
    val fakeGrps = slices.filter(_.grp == null).map(t => HtmlSliceGrp("-", List(t)))
    val realGrps = slices.filter(_.grp != null).groupBy(_.grp).map { case (grp, slices) =>
      HtmlSliceGrp(grp.name, slices)
    }
    val ret = fakeGrps ++ realGrps
    ret.sortBy(_.children.head.addr)
  }
}

object TableTreeNodeImplicits{

  implicit class GrpRegSliceExtend(grp: HtmlSliceGrp) extends TableTreeNode {
    override val name: String = grp.name
    override val children: List[TableTreeNode] = grp.children.map(new RegSliceExtend(_))
    override def tr_begin = """<tr align="center" class="grp">""".stripMargin
    override def td: String = {
      s"""      <td  rowspan="${span}">${name}</td>
         |""".stripMargin
    }
  }

  implicit class RegSliceExtend(reg: RegSlice) extends TableTreeNode {
    override val name: String = reg.name
    override val children: List[TableTreeNode] = reg.getFields.reverse.map(new FieldExtend(_))
    val regType= reg.regType.toLowerCase
    override def tr_begin = s"""<tr align="left" class="${regType}">""".stripMargin
    val addrInfo = reg.regType match {
      case "RAM" => s"0x${reg.getAddr.hexString(16)}\n ~ \n0x${reg.endaddr.hexString(16)}"
      case _     => s"0x${reg.getAddr.hexString(16)}"
    }

    override def td: String = {
      s"""            <td align="center" rowspan="${span}" class="${regType}">${addrInfo}</td>
         |            <td align="center" rowspan="${span}" class="${regType}">${reg.regType}</td>
         |            <td align="left" rowspan="${span}">${(name).toUpperCase}</td>
         |            <td class="fixWidth" align="center" rowspan="${span}">${br(reg.getDoc)} </td>
         |            <td align="center" rowspan="${span}">${reg.bi.busDataWidth}</td>
         """.stripMargin
    }
  }

  implicit class FieldExtend(fd: Field) extends TableTreeNode {
    override val name: String = fd.getName
    override val children: List[TableTreeNode] = Nil
    override def tr_begin = """<tr align="left">""".stripMargin
    override def td: String = {
      val reserved = if (fd.getAccessType == AccessType.NA) "reserved" else ""
      s"""            <td class="${reserved}" align="left"  >${Section(fd.getSection)}</td>
         |            <td class="${reserved}" align="left"  >${fd.getName}</td>
         |            <td class="${reserved}" align="center">${fd.getAccessType}</td>
         |            <td class="${reserved}" align="right" >${formatResetValue(fd.getResetValue, fd.getWidth)}</td>
         |            <td class="${reserved} fixWidth2" align="left">${br(fd.getDoc)}</td>""".stripMargin
    }
  }
}