package com.example

import android.app.Application
import com.example.data.local.SalimDatabase
import com.example.data.repository.LocalDirectoryRepository
import com.example.data.repository.LookupHistoryRepository
import com.example.data.repository.NumberIdentityRepository
import com.example.data.repository.ReportsRepository
import com.example.data.repository.SettingsRepository
import com.example.domain.usecase.DeleteLookupHistoryUseCase
import com.example.domain.usecase.LookupNumberIdentityUseCase
import com.example.domain.usecase.NormalizePhoneNumberUseCase
import com.example.domain.usecase.SaveNumberIdentityUseCase
import com.example.lookup.CommunityDirectoryProvider
import com.example.lookup.ContactsProvider
import com.example.lookup.GlobalDirectoryIntelligenceProvider
import com.example.lookup.LicensedProvider
import com.example.lookup.LocalDirectoryProvider
import com.example.lookup.PublicBusinessDirectoryProvider
import com.example.util.PhoneNumberNormalizer

class SalimApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

interface AppContainer {
    val database: SalimDatabase
    val settingsRepository: SettingsRepository
    val localDirectoryRepository: LocalDirectoryRepository
    val historyRepository: LookupHistoryRepository
    val reportsRepository: ReportsRepository
    val normalizer: PhoneNumberNormalizer
    val numberIdentityRepository: NumberIdentityRepository
    val normalizePhoneNumberUseCase: NormalizePhoneNumberUseCase
    val lookupNumberIdentityUseCase: LookupNumberIdentityUseCase
    val saveNumberIdentityUseCase: SaveNumberIdentityUseCase
    val deleteLookupHistoryUseCase: DeleteLookupHistoryUseCase
}

class DefaultAppContainer(private val application: Application) : AppContainer {

    override val database: SalimDatabase by lazy {
        SalimDatabase.getDatabase(application)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(application)
    }

    override val localDirectoryRepository: LocalDirectoryRepository by lazy {
        LocalDirectoryRepository(database.salimDao())
    }

    override val historyRepository: LookupHistoryRepository by lazy {
        LookupHistoryRepository(database.salimDao())
    }

    override val reportsRepository: ReportsRepository by lazy {
        ReportsRepository(database.salimDao())
    }

    override val normalizer: PhoneNumberNormalizer by lazy {
        PhoneNumberNormalizer()
    }

    private val contactsProvider: ContactsProvider by lazy {
        ContactsProvider(application, normalizer)
    }

    private val localDirectoryProvider: LocalDirectoryProvider by lazy {
        LocalDirectoryProvider(database.salimDao())
    }

    private val publicBusinessDirectoryProvider: PublicBusinessDirectoryProvider by lazy {
        PublicBusinessDirectoryProvider()
    }

    private val communityDirectoryProvider: CommunityDirectoryProvider by lazy {
        CommunityDirectoryProvider()
    }

    private val licensedProvider: LicensedProvider by lazy {
        LicensedProvider()
    }

    private val globalDirectoryProvider: GlobalDirectoryIntelligenceProvider by lazy {
        GlobalDirectoryIntelligenceProvider()
    }

    override val numberIdentityRepository: NumberIdentityRepository by lazy {
        NumberIdentityRepository(
            normalizer = normalizer,
            contactsProvider = contactsProvider,
            localDirectoryProvider = localDirectoryProvider,
            publicBusinessDirectoryProvider = publicBusinessDirectoryProvider,
            communityDirectoryProvider = communityDirectoryProvider,
            licensedProvider = licensedProvider,
            globalDirectoryProvider = globalDirectoryProvider,
            historyRepository = historyRepository,
            settingsRepository = settingsRepository
        )
    }

    override val normalizePhoneNumberUseCase: NormalizePhoneNumberUseCase by lazy {
        NormalizePhoneNumberUseCase(numberIdentityRepository)
    }

    override val lookupNumberIdentityUseCase: LookupNumberIdentityUseCase by lazy {
        LookupNumberIdentityUseCase(numberIdentityRepository)
    }

    override val saveNumberIdentityUseCase: SaveNumberIdentityUseCase by lazy {
        SaveNumberIdentityUseCase(localDirectoryRepository)
    }

    override val deleteLookupHistoryUseCase: DeleteLookupHistoryUseCase by lazy {
        DeleteLookupHistoryUseCase(historyRepository)
    }
}
