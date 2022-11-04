package com.rh.viacep.presenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rh.viacep.R
import com.rh.viacep.data.remote.dto.ViaCepDto
import com.rh.viacep.data.remote.dto.toViaCep
import com.rh.viacep.data.repository.ViaCepRepository
import com.rh.viacep.domain.ViaCepViewModel
import com.rh.viacep.domain.ViaCepViewModelFactory
import com.rh.viacep.domain.model.ViaCep
import com.rh.viacep.presenter.ui.Screen
import com.rh.viacep.presenter.ui.theme.OnPrimary
import com.rh.viacep.presenter.ui.theme.Primary
import com.rh.viacep.presenter.ui.theme.Secondary
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class,
        ExperimentalComposeUiApi::class
    )
    private lateinit var viewModel: ViaCepViewModel
    var searchs:MutableList<ViaCep> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ViaCepRepository()
        val viewModelFactory = ViaCepViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory)[ViaCepViewModel::class.java]

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Primary) {
                window.statusBarColor = Primary.toArgb()
                window.navigationBarColor = Primary.toArgb()
                Navigation()
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Navigation(){
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val keyboardController = LocalSoftwareKeyboardController.current
        viewModel.resultPost.observe(this@MainActivity, Observer { response ->
            val check = !response?.body()?.erro!!
            if (response.isSuccessful && check){
                keyboardController?.hide()
                response?.let {
                    val navigate = navigateToResultScreen(navController, response)
                }
            } else {
                keyboardController?.hide()
                lifecycleScope.launch{
                    snackbarHostState.showSnackbar(
                        message = "CEP Inválido ou não encontrado",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        })

        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route
        ){
            composable(route = Screen.HomeScreen.route){
                HomeScreen(navController,snackbarHostState)
            }
            composable(route = Screen.ResultScreen.route){
                ResultScreen(navController)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun HomeScreen(
        navController: NavController,
        snackbarHostState: SnackbarHostState
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Primary,

        ){
            HomeScreenContent(navController = navController, snackbarHostState = snackbarHostState)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun HomeScreenContent (
        navController: NavController,
        modifier: Modifier = Modifier,
        snackbarHostState: SnackbarHostState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(90.dp))
            Image(
                modifier = Modifier.scale(0.75f),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Consulte CEPs de todo o Brasil",
                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                fontSize = 20.sp,
                color = OnPrimary,
                style = MaterialTheme.typography.titleMedium.copy(
                    shadow = Shadow(
                        color = Color(0x40000000),
                        offset = Offset(x = 0f, y = 10f),
                        blurRadius = 7f
                    )
                )
            )
            Spacer(modifier = Modifier.height(140.dp))
            val keyboardController = LocalSoftwareKeyboardController.current
            var text = remember { mutableStateOf(TextFieldValue()) }
            Card(
                modifier = Modifier
                    .width(314.dp)
                    .height(50.dp)
                ,
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = OnPrimary),
                border = BorderStroke(4.dp, Secondary),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                var formatCep = false
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .padding(top = 2.dp, start = 8.dp)
                    ,
                    value = text.value,
                    onValueChange = {
                        if(it.text.length == 6 && !formatCep) {
                            val lastChar = it.text.last()
                            val newText = it.text.dropLast(1) + '-' + lastChar
                            text.value = TextFieldValue(newText, TextRange(newText.lastIndex+1))
                            formatCep = true
                        } else if(it.text.length < 10) text.value = it
                        if (it.text.length > 6) formatCep = false
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    maxLines = 0,
                    keyboardActions = KeyboardActions
                        (onDone = {
                            keyboardController?.hide()
                            searchAction(text,navController)
                        }),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                        fontSize = 20.sp,
                        color = Primary,
                        lineHeight = 20.sp,
                    )
                )
            }
            Spacer(modifier = Modifier.height(50.dp))

            Button(
                modifier = Modifier
                    .width(172.dp)
                    .height(50.dp)
                ,
                onClick = {
                    searchAction(text,navController)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Primary
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Text(
                    text = "Pesquisar",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.anek_malayalam_medium))
                )
            }
            Spacer(modifier = Modifier.height(150.dp))
            SnackbarHost(hostState = snackbarHostState){
                snackbarData -> Snackbar(Modifier.padding(8.dp)) {
                    Text(text = snackbarData.visuals.message)
                } 
            }
        }
    }

    private fun searchAction(text: MutableState<TextFieldValue>, navController: NavController) {
        searchs.forEach {
            if (it.cep == text.value.text.replace('-','\u0000')) {
                navigateToResultScreen(navController, it)
                return
            }
        }
        viewModel.getAllAddress(text.value.text)
    }

    private fun navigateToResultScreen(navController: NavController, viacep: ViaCep) : Boolean{
        viacep?.let {
            navController.navigate(
                route = Screen.ResultScreen.route
            )
            return true
        }
        return false
    }

    private fun navigateToResultScreen(navController: NavController, response: Response<ViaCepDto>) : Boolean{
        val viacep = response.body()?.let {
            if (
                it.bairro != null ||
                it.cep != null ||
                it.complemento != null ||
                it.ddd != null ||
                it.gia != null ||
                it.ibge != null ||
                it.localidade != null ||
                it.logradouro != null ||
                it.siafi != null ||
                it.uf != null
            ) {
                searchs.add(it.toViaCep())
                it.toViaCep()
            } else return false

            navController.navigate(
                route = Screen.ResultScreen.route
            )
            return true
        }
        return false
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ResultScreen(navController: NavController){

        val viacep = searchs.last()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Primary
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(90.dp))
                Image(
                    modifier = Modifier.scale(0.75f),
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Consulte CEPs de todo o Brasil",
                    fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                    fontSize = 20.sp,
                    color = OnPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        shadow = Shadow(
                            color = Color(0x40000000),
                            offset = Offset(x = 0f, y = 10f),
                            blurRadius = 7f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(120.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                    ,
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = OnPrimary),
                    border = BorderStroke(4.dp, Secondary),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    //var cep : ViaCep? = navController.currentBackStackEntry?.arguments?.getSerializable("cep") as ViaCep?
                    viacep.let {
                        Column(Modifier.padding(8.dp)) {
                            Text(text = "CEP: ${it.cep}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Logradouro: ${if(!it.logradouro.isNullOrEmpty()) it.logradouro else "Nenhum"}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Complemento: ${if(!it.complemento.isNullOrEmpty()) it.complemento else "Nenhum"}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Bairro: ${if(!it.bairro.isNullOrEmpty()) it.bairro else "Nenhum"}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Cidade: ${it.cidade}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Estado: ${it.estado}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = "DDD: ${it.ddd}",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_medium)),
                                fontSize = 20.sp,
                                color = Primary
                            )
                        }
                    }


                }
                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    modifier = Modifier
                        .width(172.dp)
                        .height(50.dp)
                    ,
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = Primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Voltar",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.anek_malayalam_medium))
                    )
                }
            }
        }
    }
}