package com.example.tailorflow.ui.screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tailorflow.data.model.Commande
import com.example.tailorflow.data.remote.SupabaseClientProvider
import com.example.tailorflow.ui.theme.*
import com.example.tailorflow.viewmodel.CommandeViewModel
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    vm: CommandeViewModel,
    navController: NavController
) {
    val editingCmd by vm.commandeEnEdition.collectAsState()

    var nom by remember { mutableStateOf(editingCmd?.nomClient ?: "") }
    var telephone by remember { mutableStateOf(editingCmd?.telephone ?: "") }
    var epaules by remember { mutableStateOf(editingCmd?.epaules?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var poitrine by remember { mutableStateOf(editingCmd?.poitrine?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var taille by remember { mutableStateOf(editingCmd?.taille?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var longueur by remember { mutableStateOf(editingCmd?.longueur?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var manches by remember { mutableStateOf(editingCmd?.manches?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var hanche by remember { mutableStateOf(editingCmd?.hanche?.let { if(it == 0f) "" else it.toString() } ?: "") }
    var dateLivraison by remember { mutableStateOf(editingCmd?.dateLivraison ?: LocalDate.now().plusDays(7).toString()) }
    var statut by remember { mutableStateOf(editingCmd?.statut ?: "En attente") }
    var isUrgent by remember { mutableStateOf(editingCmd?.isUrgent ?: false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf(editingCmd?.imageUrl ?: "") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Modal state for Status Selection
    var showStatusDialog by remember { mutableStateOf(false) }
    val statuses = listOf("En attente", "Coupe en cours", "Couture", "Terminé")

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (editingCmd == null) "Nouvelle commande" else "Modifier commande", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { 
                        vm.editCommande(null)
                        navController.popBackStack() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Retour", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            loading = true
                            scope.launch {
                                try {
                                    val imageUrl = if (imageUri != null) {
                                        uploadImage(imageUri!!, context)
                                    } else {
                                        existingImageUrl
                                    }
                                    
                                    val newCmd = Commande(
                                        id = editingCmd?.id,
                                        nomClient = nom,
                                        telephone = telephone,
                                        epaules = epaules.replace(",", ".").toFloatOrNull() ?: 0f,
                                        poitrine = poitrine.replace(",", ".").toFloatOrNull() ?: 0f,
                                        taille = taille.replace(",", ".").toFloatOrNull() ?: 0f,
                                        longueur = longueur.replace(",", ".").toFloatOrNull() ?: 0f,
                                        manches = manches.replace(",", ".").toFloatOrNull() ?: 0f,
                                        hanche = hanche.replace(",", ".").toFloatOrNull() ?: 0f,
                                        imageUrl = imageUrl,
                                        dateLivraison = dateLivraison,
                                        statut = statut,
                                        isUrgent = isUrgent
                                    )
                                    vm.sauvegarder(newCmd)
                                    vm.editCommande(null)
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = !loading && nom.isNotBlank()
                    ) {
                        Surface(
                            color = TailorGold,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (loading) {
                                CircularProgressIndicator(color = TailorDarkBg, strokeWidth = 2.dp, modifier = Modifier.padding(6.dp))
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Enregistrer", tint = TailorDarkBg, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("Informations client")
            TailorTextField(
                value = nom,
                onValueChange = { nom = it },
                label = "Nom du client",
                icon = Icons.Default.Person
            )
            TailorTextField(
                value = telephone,
                onValueChange = { telephone = it },
                label = "Téléphone",
                icon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Mesures (en cm)")
            Row(modifier = Modifier.fillMaxWidth()) {
                TailorMeasureField(value = epaules, onValueChange = { epaules = it }, label = "Épaules", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                TailorMeasureField(value = poitrine, onValueChange = { poitrine = it }, label = "Poitrine", modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TailorMeasureField(value = taille, onValueChange = { taille = it }, label = "Taille", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                TailorMeasureField(value = longueur, onValueChange = { longueur = it }, label = "Longueur", modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TailorMeasureField(value = manches, onValueChange = { manches = it }, label = "Manches", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                TailorMeasureField(value = hanche, onValueChange = { hanche = it }, label = "Hanche", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Photo du modèle / tissu")
            ImagePickerBox(imageUri, existingImageUrl) { launcher.launch("image/*") }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Options")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TailorSurface, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = if (isUrgent) StatusUrgent else TailorTextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Marquer comme Urgent", color = Color.White)
                }
                Switch(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TailorGold,
                        checkedTrackColor = TailorGold.copy(alpha = 0.5f),
                        uncheckedThumbColor = TailorTextSecondary,
                        uncheckedTrackColor = TailorCardBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Date de livraison")
            TailorSelectorField(
                value = try { LocalDate.parse(dateLivraison).format(DateTimeFormatter.ofPattern("dd / MM / yyyy")) } catch(e:Exception) { dateLivraison },
                icon = Icons.Default.DateRange,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Statut")
            TailorSelectorField(
                value = statut,
                onClick = { showStatusDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Changer le statut") },
            text = {
                Column {
                    statuses.forEach { s ->
                        Text(
                            text = s,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    statut = s
                                    showStatusDialog = false
                                }
                                .padding(16.dp),
                            color = if (statut == s) TailorGold else Color.White
                        )
                    }
                }
            },
            confirmButton = {},
            containerColor = TailorSurface
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dateLivraison = java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) { Text("OK", color = TailorGold) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TailorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        placeholder = { Text(label, color = TailorTextSecondary) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TailorTextSecondary) },
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TailorGold,
            unfocusedBorderColor = TailorCardBorder,
            focusedContainerColor = TailorSurface,
            unfocusedContainerColor = TailorSurface,
            cursorColor = TailorGold,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TailorMeasureField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(label, color = TailorTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TailorGold,
                unfocusedBorderColor = TailorCardBorder,
                focusedContainerColor = TailorSurface,
                unfocusedContainerColor = TailorSurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun ImagePickerBox(imageUri: Uri?, existingUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .border(1.dp, TailorCardBorder, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(TailorSurface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else if (existingUrl.isNotEmpty()) {
            AsyncImage(model = existingUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = TailorGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter une photo", color = TailorTextSecondary)
            }
        }
    }
}

@Composable
fun TailorSelectorField(value: String, icon: ImageVector? = null, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TailorSurface, RoundedCornerShape(12.dp))
            .border(1.dp, TailorCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = TailorTextSecondary)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(value, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TailorTextSecondary)
        }
    }
}

suspend fun uploadImage(uri: Uri, context: android.content.Context): String {
    return try {
        // 1. Récupérer l'ID de l'utilisateur pour créer un dossier personnel dans le bucket
        val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id ?: return ""

        // 2. Lire les octets de l'image
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return ""

        // 3. Créer un nom de fichier unique (ex: user123/img_170000000.jpg)
        val fileName = "$userId/img_${System.currentTimeMillis()}.jpg"

        // 4. Envoyer au bucket "images"
        SupabaseClientProvider.client.storage
            .from("images")
            .upload(fileName, bytes, upsert = true)

        // 5. Récupérer et retourner l'URL publique
        val url = SupabaseClientProvider.client.storage
            .from("images")
            .publicUrl(fileName)

        Log.d("STORAGE", "Image uploadée avec succès: $url")
        url

    } catch (e: Exception) {
        Log.e("STORAGE", "Erreur lors de l'upload: ${e.message}")
        // On affiche un message d'erreur à l'utilisateur
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Erreur Photo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
        ""
    }
}
