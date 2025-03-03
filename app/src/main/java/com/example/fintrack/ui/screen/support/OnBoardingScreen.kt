package com.example.fintrack.ui.screen.support

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fintrack.R
import com.example.fintrack.ui.util.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(rootNavController:NavHostController,viewModel: MainViewModel){

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        3
    }

    var imagePainter = remember{ 0 }
    var heightImage = remember{ 0 }
    var title = remember{ "" }
    var description = remember { "" }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        HorizontalPager(state = pagerState) {
            when(pagerState.currentPage){
                0 ->{
                    imagePainter = R.drawable.undraw_mobile_content_xvgr
                    heightImage = 300
                    title = "Welcome to Expense Tracker"
                    description = "Easily manage your finances. Expense Tracker helps you record and manage your daily expenses effortlessly"
                }
                1->{
                    imagePainter = R.drawable.stat
                    heightImage = 200
                    title = "Record Expenses Quickly"
                    description = "Expense Tracker makes daily expense tracking a breeze. Just a few taps to record your transactions"

                }
                2->{
                    imagePainter = R.drawable.real_time
                    heightImage = 260
                    title = "Real-time Financial Monitoring"
                    description = "Monitor your finances instantly. Expense Tracker provides clear insights with informative graphs and reports for wise financial decisions"
                }
            }
                OnBoardingItem(imagePainter = imagePainter, heightImage = heightImage, title = title, description = description)
        }
        if(pagerState.currentPage == 2){
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 100.dp, bottom = 40.dp).padding(horizontal = 10.dp),
                onClick = {
                    viewModel.saveOnBoarding()
                    rootNavController.navigate("mainScreen")
                          },
                shape = RoundedCornerShape(10),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.background),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Get Started", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }
        }else{
            IndicatorNavigation(pagerState,scope)
        }
        
    }

}

@Composable
fun OnBoardingItem(imagePainter:Int,heightImage: Int,title:String,description:String){
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imagePainter),
            contentDescription = title,
            Modifier.height((heightImage).dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            modifier = Modifier.fillMaxWidth(0.75f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndicatorNavigation(pagerState: PagerState,scope:CoroutineScope){
    Column(Modifier.padding(top = 100.dp, bottom = 80.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(3){
                val selected = pagerState.currentPage == it
                Box(
                    Modifier
                        .size(18.dp)
                        .clip(shape = CircleShape)
                        .background(color = if (selected) Color.Black else Color.Gray)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(it)
                            }
                        }

                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
