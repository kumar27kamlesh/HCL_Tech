package com.hcl.ewallet.merchant.service;

import com.hcl.ewallet.merchant.entity.*;
import com.hcl.ewallet.merchant.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceTest {

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    MerchantWalletRepository walletRepository;

    @Mock
    MerchantTransactionRepository transactionRepository;

    @Mock
    SettlementRepository settlementRepository;

    @InjectMocks
    MerchantService merchantService;

    // =========================
    // Create Merchant
    // =========================

    @Test
    void createMerchant_success() {

        String id = merchantService.createMerchant("Amazon", "INR");

        assertNotNull(id);

        verify(merchantRepository).save(any());
        verify(walletRepository).save(any());
    }

    // =========================
    // Credit Merchant
    // =========================

    @Test
    void creditMerchant_success() {

        Merchant merchant = new Merchant();
        merchant.setMerchantId("M1");

        MerchantWallet wallet = new MerchantWallet();
        wallet.setMerchantId("M1");
        wallet.setBalance(new BigDecimal("1000"));
        wallet.setCurrency("INR");

        // MOCK ALL LOOKUPS

        when(walletRepository.findByMerchantId(anyString()))
                .thenReturn(Optional.of(wallet));

        merchantService.creditMerchant(
                "M1",
                new BigDecimal("1000"),
                "C1"
        );

        assertEquals(new BigDecimal("1980"), wallet.getBalance());

        verify(transactionRepository).save(any());
        verify(settlementRepository).save(any());
    }

    // =========================
    // Get Wallet
    // =========================

    @Test
    void getWallet_success() {

        MerchantWallet wallet = new MerchantWallet();
        wallet.setMerchantId("M1");
        wallet.setBalance(new BigDecimal("500"));
        wallet.setCurrency("INR");

        when(walletRepository.findByMerchantId("M1"))
                .thenReturn(Optional.of(wallet));

        var response = merchantService.getWalletByMerchantId("M1");

        assertEquals(new BigDecimal("500"), response.getBalance());
    }

    // =========================
    // Get Transactions
    // =========================

    @Test
    void getTransactions_success() {

        MerchantTransaction txn = new MerchantTransaction();
        txn.setMerchantId("M1");

        when(transactionRepository.findByMerchantId("M1"))
                .thenReturn(List.of(txn));

        var list = merchantService.getTransactionsByMerchant("M1");

        assertEquals(1, list.size());
    }
}
