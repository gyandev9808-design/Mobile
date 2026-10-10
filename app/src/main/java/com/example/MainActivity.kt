package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddMotionOrPointDialog
import com.example.ui.components.DelegateXTopBar
import com.example.ui.components.VoteResolutionDialog
import com.example.ui.screens.ChairDashboardScreen
import com.example.ui.screens.ChairRegistrationScreen
import com.example.ui.screens.CommitteeRosterScreen
import com.example.ui.screens.DelegateDashboardScreen
import com.example.ui.screens.DelegateRegistrationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RulesOfProcedureDialog
import com.example.ui.screens.ServerConnectionDialog
import com.example.ui.screens.SessionLogScreen
import com.example.ui.theme.DelegateXTheme
import com.example.ui.theme.DiplomaticNavy
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ConferenceViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: ConferenceViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DelegateXTheme {
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        // BackHandler: return to Home from sub-screens
        if (state.currentScreen != AppScreen.HOME) {
          BackHandler {
            viewModel.navigateTo(AppScreen.HOME)
          }
        }

        // Show banner notifications in snackbar or top pill
        LaunchedEffect(state.bannerNotification) {
          state.bannerNotification?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissNotification()
          }
        }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .testTag("delegatex_main_scaffold"),
          topBar = {
            DelegateXTopBar(
              currentScreen = state.currentScreen,
              serverAlias = state.serverHostAlias,
              isServerConnected = state.isServerConnected,
              serverLatencyMs = state.serverLatencyMs,
              onNavigateBack = { viewModel.navigateTo(AppScreen.HOME) },
              onOpenServerDialog = { viewModel.toggleServerDialog(true) },
              onOpenRopGuide = { viewModel.toggleRopGuideDialog(true) },
              onOpenRoster = { viewModel.navigateTo(AppScreen.COMMITTEE_ROSTER) }
            )
          },
          bottomBar = {
            NavigationBar(
              modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("delegatex_bottom_navigation")
            ) {
              NavigationBarItem(
                selected = state.currentScreen == AppScreen.HOME,
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_tab_home")
              )

              // Portal tab: either Dais if Chair registered, or Credentials if Delegate registered
              val portalScreen = when {
                state.currentChair != null -> AppScreen.CHAIR_PORTAL
                state.currentDelegate != null -> AppScreen.DELEGATE_PORTAL
                else -> AppScreen.REGISTER_DELEGATE
              }
              val portalLabel = when {
                state.currentChair != null -> "Dais"
                state.currentDelegate != null -> "Placard"
                else -> "Register"
              }
              val portalIcon = when {
                state.currentChair != null -> Icons.Default.Gavel
                else -> Icons.Default.Badge
              }

              NavigationBarItem(
                selected = state.currentScreen == AppScreen.CHAIR_PORTAL || state.currentScreen == AppScreen.DELEGATE_PORTAL,
                onClick = { viewModel.navigateTo(portalScreen) },
                icon = { Icon(portalIcon, contentDescription = portalLabel) },
                label = { Text(portalLabel, fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_tab_portal")
              )

              NavigationBarItem(
                selected = state.currentScreen == AppScreen.COMMITTEE_ROSTER,
                onClick = { viewModel.navigateTo(AppScreen.COMMITTEE_ROSTER) },
                icon = { Icon(Icons.Default.People, contentDescription = "Roster") },
                label = { Text("Roster", fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_tab_roster")
              )

              NavigationBarItem(
                selected = state.currentScreen == AppScreen.SESSION_LOG,
                onClick = { viewModel.navigateTo(AppScreen.SESSION_LOG) },
                icon = { Icon(Icons.Default.DynamicFeed, contentDescription = "Feed") },
                label = { Text("Feed", fontSize = 11.sp) },
                modifier = Modifier.testTag("nav_tab_feed")
              )
            }
          },
          snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (state.currentScreen) {
              AppScreen.HOME -> {
                HomeScreen(
                  committees = state.committees,
                  delegates = state.delegates,
                  chairs = state.chairs,
                  currentChair = state.currentChair,
                  currentDelegate = state.currentDelegate,
                  sessionActions = state.sessionActions,
                  serverAlias = state.serverHostAlias,
                  isServerConnected = state.isServerConnected,
                  onNavigateTo = { viewModel.navigateTo(it) },
                  onSelectCommittee = { viewModel.selectCommittee(it) },
                  onSelectChair = { viewModel.setCurrentChair(it) },
                  onSelectDelegate = { viewModel.setCurrentDelegate(it) }
                )
              }

              AppScreen.REGISTER_CHAIR -> {
                ChairRegistrationScreen(
                  committees = state.committees,
                  onRegister = { name, email, inst, comm, pos, pin, stmt ->
                    viewModel.registerChair(
                      fullName = name,
                      email = email,
                      institution = inst,
                      committee = comm,
                      position = pos,
                      pin = pin,
                      openingStatement = stmt,
                      onSuccess = { viewModel.navigateTo(AppScreen.CHAIR_PORTAL) }
                    )
                  },
                  onCancel = { viewModel.navigateTo(AppScreen.HOME) }
                )
              }

              AppScreen.REGISTER_DELEGATE -> {
                DelegateRegistrationScreen(
                  committees = state.committees,
                  onRegister = { name, email, inst, comm, country, code, exp, paper ->
                    viewModel.registerDelegate(
                      fullName = name,
                      email = email,
                      institution = inst,
                      committee = comm,
                      country = country,
                      countryCode = code,
                      experienceLevel = exp,
                      positionPaperTopic = paper,
                      onSuccess = { viewModel.navigateTo(AppScreen.DELEGATE_PORTAL) }
                    )
                  },
                  onCancel = { viewModel.navigateTo(AppScreen.HOME) }
                )
              }

              AppScreen.CHAIR_PORTAL -> {
                val currentChair = state.currentChair
                val activeComm = state.committees.firstOrNull { it.id == state.selectedCommitteeId }
                if (currentChair != null) {
                  ChairDashboardScreen(
                    chair = currentChair,
                    committee = activeComm,
                    delegates = state.delegates,
                    gslSpeaker = state.currentGslSpeaker,
                    gslTimerSeconds = state.gslTimerSecondsRemaining,
                    isGslTimerRunning = state.isGslTimerRunning,
                    activeCaucus = state.activeCaucus,
                    onUpdateRollCall = { del, status -> viewModel.updateDelegateRollCall(del, status) },
                    onSetGslSpeaker = { viewModel.setGslSpeaker(it) },
                    onStartGslTimer = { viewModel.startGslTimer() },
                    onPauseGslTimer = { viewModel.pauseGslTimer() },
                    onResetGslTimer = { viewModel.resetGslTimer() },
                    onStartCaucus = { topic, isMod, total, spk ->
                      viewModel.startCaucus(topic, isMod, total, spk)
                    },
                    onToggleCaucusPause = { viewModel.toggleCaucusPause() },
                    onEndCaucus = { viewModel.endCaucus() },
                    onOpenAddMotionDialog = { viewModel.toggleAddMotionDialog(true) },
                    onOpenVoteResolutionDialog = { viewModel.toggleVoteResolutionDialog(true) }
                  )
                } else {
                  ChairRegistrationScreen(
                    committees = state.committees,
                    onRegister = { name, email, inst, comm, pos, pin, stmt ->
                      viewModel.registerChair(
                        fullName = name,
                        email = email,
                        institution = inst,
                        committee = comm,
                        position = pos,
                        pin = pin,
                        openingStatement = stmt,
                        onSuccess = { viewModel.navigateTo(AppScreen.CHAIR_PORTAL) }
                      )
                    },
                    onCancel = { viewModel.navigateTo(AppScreen.HOME) }
                  )
                }
              }

              AppScreen.DELEGATE_PORTAL -> {
                val currentDel = state.currentDelegate
                val activeComm = state.committees.firstOrNull { it.id == state.selectedCommitteeId }
                if (currentDel != null) {
                  DelegateDashboardScreen(
                    delegate = currentDel,
                    committee = activeComm,
                    allCommitteeDelegates = state.delegates,
                    currentGslSpeaker = state.currentGslSpeaker,
                    gslTimerSeconds = state.gslTimerSecondsRemaining,
                    isGslTimerRunning = state.isGslTimerRunning,
                    activeCaucus = state.activeCaucus,
                    onRaisePlacardGsl = { country ->
                      viewModel.setGslSpeaker(country)
                      viewModel.submitMotionOrPoint(
                        actorName = "Delegate of $country",
                        actorRole = "DELEGATE",
                        type = "GSL_REQUEST",
                        description = "$country raised placard and requested floor on General Speakers List."
                      )
                    },
                    onOpenRaiseMotionDialog = { viewModel.toggleAddMotionDialog(true) },
                    onOpenRopGuide = { viewModel.toggleRopGuideDialog(true) }
                  )
                } else {
                  DelegateRegistrationScreen(
                    committees = state.committees,
                    onRegister = { name, email, inst, comm, country, code, exp, paper ->
                      viewModel.registerDelegate(
                        fullName = name,
                        email = email,
                        institution = inst,
                        committee = comm,
                        country = country,
                        countryCode = code,
                        experienceLevel = exp,
                        positionPaperTopic = paper,
                        onSuccess = { viewModel.navigateTo(AppScreen.DELEGATE_PORTAL) }
                      )
                    },
                    onCancel = { viewModel.navigateTo(AppScreen.HOME) }
                  )
                }
              }

              AppScreen.COMMITTEE_ROSTER -> {
                CommitteeRosterScreen(
                  committees = state.committees,
                  delegates = state.delegates,
                  chairs = state.chairs,
                  selectedCommitteeId = state.selectedCommitteeId,
                  onSelectCommittee = { viewModel.selectCommittee(it) },
                  onSelectDelegate = { del ->
                    viewModel.setCurrentDelegate(del)
                    viewModel.navigateTo(AppScreen.DELEGATE_PORTAL)
                  },
                  onSelectChair = { ch ->
                    viewModel.setCurrentChair(ch)
                    viewModel.navigateTo(AppScreen.CHAIR_PORTAL)
                  }
                )
              }

              AppScreen.SESSION_LOG -> {
                SessionLogScreen(
                  actions = state.sessionActions,
                  committeeId = state.selectedCommitteeId,
                  serverAlias = state.serverHostAlias
                )
              }
            }

            // Dialogs
            if (state.showServerDialog) {
              ServerConnectionDialog(
                serverUrl = state.serverUrl,
                serverAlias = state.serverHostAlias,
                isConnected = state.isServerConnected,
                statusText = state.serverStatusText,
                latencyMs = state.serverLatencyMs,
                isChecking = state.isCheckingConnection,
                onDismiss = { viewModel.toggleServerDialog(false) },
                onTestConnection = { viewModel.checkServerConnection(it) }
              )
            }

            if (state.showRopGuideDialog) {
              RulesOfProcedureDialog(
                onDismiss = { viewModel.toggleRopGuideDialog(false) }
              )
            }

            if (state.showAddMotionDialog) {
              val actor = state.currentDelegate?.country ?: state.currentChair?.fullName ?: "Delegate"
              val role = if (state.currentChair != null) "CHAIR" else "DELEGATE"
              AddMotionOrPointDialog(
                defaultActorName = actor,
                defaultRole = role,
                onDismiss = { viewModel.toggleAddMotionDialog(false) },
                onSubmit = { name, r, type, desc ->
                  viewModel.submitMotionOrPoint(name, r, type, desc)
                }
              )
            }

            if (state.showVoteResolutionDialog) {
              VoteResolutionDialog(
                onDismiss = { viewModel.toggleVoteResolutionDialog(false) },
                onRecordVote = { res, spon, favor, against, abstain ->
                  viewModel.recordResolutionVote(res, spon, favor, against, abstain)
                }
              )
            }
          }
        }
      }
    }
  }
}
