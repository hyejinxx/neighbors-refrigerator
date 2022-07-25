package com.neighbor.neighborsrefrigerator.scenarios.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.neighbor.neighborsrefrigerator.data.PostData
import com.neighbor.neighborsrefrigerator.scenarios.main.NAV_ROUTE
import com.neighbor.neighborsrefrigerator.scenarios.main.post.SearchBox
import com.neighbor.neighborsrefrigerator.viewmodels.SharePostViewModel

@Composable
fun SeekPostScreen(
    sharePostViewModel: SharePostViewModel,
    route: NAV_ROUTE,
    navHostController: NavHostController
) {
    Column() {
        SearchBox(sharePostViewModel, "seek")
        Text(
            text = "# 이웃 주민과 함께 어쩌구 ~",
            modifier = Modifier.padding(start = 30.dp, end = 15.dp, top = 30.dp, bottom = 10.dp),
            fontSize = 20.sp
        )
        SeekPostList(sharePostViewModel.posts.collectAsState(), route = route, navHostController = navHostController)
    }
}
@Composable
fun SeekPostList(posts : State<List<PostData>?>,
                 route: NAV_ROUTE,
                 navHostController: NavHostController){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 30.dp, end = 30.dp)) {
        posts.value?.let {
            it.forEach {
                SeekItem(postData = it, distance = 3.4, route = route, navHostController = navHostController)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SeekItem(postData: PostData, distance : Double,
             route: NAV_ROUTE,
             navHostController: NavHostController){
    Card(
        onClick = {navHostController.navigate(route = "${route.routeName}/${postData.title}")},
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .fillMaxWidth(), elevation = 0.dp) {
        Column() {
        Text(text = postData.title!!, fontSize = 15.sp, modifier = Modifier.padding(bottom = 7.dp))
        Text(text = postData.content!!, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1,modifier = Modifier.padding(bottom = 10.dp))
        Text(text = "내 위치에서 ${distance}km", fontSize = 10.sp, color = Color.DarkGray)
        Text(text = "업로드 : 3분전", fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}

@Preview
@Composable
fun Preview22() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        var content by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, end = 30.dp, start = 30.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            val cornersize = CornerSize(20.dp)
            OutlinedTextField(value = content, onValueChange = { content = it }, modifier = Modifier
                .fillMaxWidth()
                .size(45.dp), shape = MaterialTheme.shapes.large.copy(cornersize))

            IconButton(onClick = {  }) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
        }
        Text(
            text = "# 이웃 주민과 함께 어쩌구 ~",
            modifier = Modifier.padding(start = 30.dp, end = 15.dp, top = 25.dp, bottom = 10.dp),
            fontSize = 15.sp
        )

    }
}