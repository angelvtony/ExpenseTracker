package com.example.fintrack.ui.screen.summary

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fintrack.R
import com.example.fintrack.data.Expense
import com.example.fintrack.data.currencySymbol
import com.example.fintrack.ui.util.ConvertDecimal
import com.example.fintrack.ui.util.MainViewModel
import com.example.fintrack.ui.util.TabSummaryList
import com.example.fintrack.ui.util.categoriesImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SummaryScreen(innerPadding:PaddingValues = PaddingValues(20.dp),viewModel: MainViewModel){
    var tabIndex = remember{ mutableIntStateOf(0) }
    val expenseList = when (tabIndex.value) {
        0-> viewModel.getAllExpenseMonth.collectAsState(initial = listOf())
        else -> viewModel.getAllExpenseYear.collectAsState(initial = listOf())
    }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            generateExpenseReport(context, expenseList.value)
        } else {
            Toast.makeText(context, "Storage Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(10.dp)) {
        Text(
            text = "Your Summary",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Tabs(tabIndex)
        Spacer(Modifier.height(16.dp))
        if (expenseList.value.isEmpty()){
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(48.dp))
                Image(
                    painter = painterResource(id = R.drawable.undraw_no_data_re_kwbl),
                    contentDescription = "No Expense Yet",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No Expense Yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }else{
            ExpenseChart(expenseList)
            SummaryList(expenseList)
            Button(
                onClick = {
                    when {
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                        else -> {
                            generateExpenseReport(context, expenseList.value)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Download Expense Report")
            }
        }

    }
}

@Composable
fun Tabs(tabIndex:MutableIntState){
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TabSummaryList.forEachIndexed(){
                index,items ->
            val isSelected = tabIndex.value == index
            OutlinedButton(
                onClick = { tabIndex.value = index  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = if(isSelected)R.color.background else R.color.secondary),
                    contentColor = colorResource(id = if(isSelected)R.color.secondary else R.color.background)
                )
            ) {
                Text(items.title)
            }
            Spacer(Modifier.width(10.dp))
        }

    }

}

@Composable
fun SummaryList(expenseList: State<List<Expense>>){
    val uniqueCategories = expenseList.value.map { it.category }.distinct()
    val totalAmount = expenseList.value.sumOf { it.amount }
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(5)
            )
    ) {
        LazyColumn {
            items(uniqueCategories){
                categories ->
                CardSummaryItem(categories,expenseList,totalAmount)
            }
        }
    }
}

@Composable
fun CardSummaryItem(categories:String,expenseList: State<List<Expense>>,totalAmount:Int){
    val amountCategory =  expenseList.value.filter { it.category == categories }.sumOf { it.amount }
    val percentage = if (totalAmount != 0) {
        (amountCategory.toDouble() / totalAmount) * 100
    } else {
        0.0
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                Icon(
                    painter = painterResource(id = categoriesImage[categories]!!),
                    modifier = Modifier.size(32.dp),
                    contentDescription = categories,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.width(160.dp)
                ) {
                    Text(
                        text = categories,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = (percentage/100).toFloat(),
                        color = colorResource(id = R.color.background)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${currencySymbol} ${ConvertDecimal(amountCategory)}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${percentage.toInt()}%",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ExpenseChart(expenseList: State<List<Expense>>) {
    val totalAmount = expenseList.value.sumOf { it.amount }
    val categoryAmounts = expenseList.value
        .groupBy { it.category }
        .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Cyan, Color.Magenta)

    if (totalAmount == 0) return

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(16.dp)
    ) {
        val pieSlices = mutableListOf<Float>()
        var startAngle = -90f
        categoryAmounts.values.forEach { amount ->
            pieSlices.add((amount.toFloat() / totalAmount) * 360f)
        }

        categoryAmounts.entries.forEachIndexed { index, entry ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = pieSlices[index],
                useCenter = true
            )
            startAngle += pieSlices[index]
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        categoryAmounts.entries.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(colors[index % colors.size], shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${entry.key}: ${ConvertDecimal(entry.value)}", fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@SuppressLint("ResourceAsColor")
fun generateExpenseReport(context: Context, expenses: List<Expense>) {
    if (expenses.isEmpty()) {
        Toast.makeText(context, "No expenses to save!", Toast.LENGTH_SHORT).show()
        return
    }

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply {
        color = R.color.background
        textSize = 14f
    }

    var yPosition = 40f
    canvas.drawText("Expense Report", 10f, yPosition, paint)
    yPosition += 30f

    expenses.forEach { expense ->
        canvas.drawText("${expense.category}: ${expense.amount}", 10f, yPosition, paint)
        yPosition += 20f
    }

    pdfDocument.finishPage(page)

    try {
        val fileName = "Expense_Report_${System.currentTimeMillis()}.pdf"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 🔹 Scoped Storage for Android 10+
            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { outputStream ->
                contentResolver.openOutputStream(outputStream)?.use { pdfDocument.writeTo(it) }
                Toast.makeText(context, "PDF saved in Downloads", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 🔹 Save to external storage for Android 9 and below
            val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/$fileName"
            val file = File(filePath)
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved at: $filePath", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}
