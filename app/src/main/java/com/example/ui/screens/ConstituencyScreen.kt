package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbConstituency
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricGauge
import com.example.viewmodel.CivicLensViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstituencyScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val constituencies by viewModel.constituencies.collectAsState()
    val isSeeding by viewModel.isSeeding.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var selectedConstituency by remember { mutableStateOf<DbConstituency?>(null) }

    // Initialize defaults if list loaded
    LaunchedEffect(constituencies) {
        if (constituencies.isNotEmpty() && selectedConstituency == null) {
            selectedConstituency = constituencies.firstOrNull()
        }
    }

    // Filter constituencies based on query
    val filteredConstituencies = constituencies.filter {
        it.name.contains(searchInput, ignoreCase = true) ||
                it.pinCodes.contains(searchInput, ignoreCase = true) ||
                it.state.contains(searchInput, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Constituency Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("constituency_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            
            // Search Bar Input
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("Search by constituency name or PIN code...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("constituency_search_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // Dynamic suggestions row based on search
            if (searchInput.isNotEmpty() && filteredConstituencies.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredConstituencies) { con ->
                        Text(
                            text = "${con.name} (${con.state})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedConstituency = con
                                    searchInput = ""
                                }
                                .padding(12.dp)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedConstituency != null) {
                    val con = selectedConstituency!!

                    // 1. Basic Stats Card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth().testTag("constituency_details_card")) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = con.state.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Population: ${con.population}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = con.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RepresentativeCard(label = "Member of Parliament (MP)", name = con.mpName, modifier = Modifier.weight(1f))
                                RepresentativeCard(label = "Member of Assembly (MLA)", name = con.mlaName, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 2. Budget Allocation
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("PUBLIC BUDGET ALLOCATION", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                    Text(con.budgetAllocation, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    // 3. Infrastructure and Metric Gauges
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth().testTag("constituency_metrics_card")) {
                            Text("Local Public Infrastructure", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            MetricGauge("Roads Progress", con.roadsProgress, color = Color(0xFFE65100))
                            Spacer(modifier = Modifier.height(10.dp))
                            MetricGauge("Tap Water Progress (Har Ghar Jal)", con.waterProgress, color = Color(0xFF0277BD))
                            Spacer(modifier = Modifier.height(10.dp))
                            MetricGauge("Electricity Connectivity", con.electricityProgress, color = Color(0xFFFBC02D))
                            Spacer(modifier = Modifier.height(10.dp))
                            MetricGauge("High Speed Internet Progress", con.internetProgress, color = Color(0xFF2E7D32))
                        }
                    }

                    // 4. Social Infrastructure
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SocialStatCard("Primary/High Schools", "${con.schoolsCount}", Icons.Default.School, Modifier.weight(1f))
                            SocialStatCard("Public Hospitals/Clinics", "${con.hospitalsCount}", Icons.Default.LocalHospital, Modifier.weight(1f))
                        }
                    }

                    // 5. Development Projects
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Key Development Projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))

                            con.developmentProjects.forEachIndexed { index, project ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(project, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    // 6. Ground with AI query button
                    item {
                        Button(
                            onClick = {
                                viewModel.askAssistant("Provide verified, neutral progress tracking report on constituency ${con.name} in ${con.state} based on Lok Sabha debates and official ministerial allocations.")
                                onNavigateToAssistant()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ground_constituency_ai_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze Local Budget & Debates with AI", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSeeding) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text(
                                    text = "No constituency data available.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepresentativeCard(
    label: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun SocialStatCard(
    label: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(count, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
