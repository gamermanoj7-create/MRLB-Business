package com.mrlb.business.utils
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.mrlb.business.data.AppDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {
    private val dateFmt=SimpleDateFormat("dd-MM-yyyy HH:mm",Locale.getDefault())

    fun createCsv(context:Context, db:AppDatabase):File {
        val dir=File(context.cacheDir,"exports").apply{mkdirs()}
        val f=File(dir,"mrlb_report_${System.currentTimeMillis()}.csv")
        f.writeText("MRLB Business Report\nGenerated,${dateFmt.format(Date())}\n\n")
        f.appendText("Orders\nID,Customer ID,Date,Total,Paid,Due,Status\n")
        // DAO collection is handled by caller in the async activity.
        return f
    }

    fun writeCsv(file:File, rows:List<Array<String>>) {
        file.appendText(rows.joinToString("\n"){ row -> row.joinToString(","){ csv(it) } })
    }
    private fun csv(s:String)= ""${s.replace(""","""")}""

    fun createPdf(context:Context,title:String,lines:List<String>):File {
        val dir=File(context.cacheDir,"exports").apply{mkdirs()}
        val file=File(dir,"${title.replace(" ","_")}_${System.currentTimeMillis()}.pdf")
        val doc=PdfDocument()
        val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create())
        val canvas=page.canvas; val paint=Paint().apply{textSize=14f}
        var y=45f
        canvas.drawText("MR LUXE BOX & BAGS",40f,y,paint); y+=28
        canvas.drawText(title,40f,y,paint); y+=28
        lines.forEach {
            if(y>800f){doc.finishPage(page);return@forEach}
            canvas.drawText(it.take(90),40f,y,paint); y+=22
        }
        doc.finishPage(page)
        file.outputStream().use{doc.writeTo(it)}
        doc.close()
        return file
    }

    fun share(context:Context,file:File,mime:String) {
        val uri:Uri=FileProvider.getUriForFile(context,context.packageName+".files",file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{
            type=mime; putExtra(Intent.EXTRA_STREAM,uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },"Share"))
    }

    fun whatsapp(context:Context,phone:String,message:String) {
        val clean=phone.filter{it.isDigit()}
        val uri=Uri.parse("https://wa.me/$clean?text="+Uri.encode(message))
        context.startActivity(Intent(Intent.ACTION_VIEW,uri))
    }
}
