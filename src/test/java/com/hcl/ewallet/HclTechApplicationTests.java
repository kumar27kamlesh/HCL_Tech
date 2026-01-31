package com.hcl.ewallet;

import com.hcl.ewallet.wallet.dto.TransactionRequest;
import com.hcl.ewallet.wallet.Wallet;
import com.hcl.ewallet.wallet.WalletEntry;
import com.hcl.ewallet.wallet.exception.InsufficientBalanceException;
import com.hcl.ewallet.wallet.WalletEntryRepository;
import com.hcl.ewallet.wallet.WalletRepository;
import com.hcl.ewallet.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HclTechApplicationTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletEntryRepository walletEntryRepository;

    @InjectMocks
    private WalletService walletService;

    @Captor // Captures arguments for detailed assertions
    private ArgumentCaptor<WalletEntry> entryCaptor;

    private Wallet mockWallet;
    private TransactionRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Mock Data
        mockWallet = new Wallet();
        mockWallet.setId(1L);
        mockWallet.setUserId(101L);
        mockWallet.setBalance(new BigDecimal("100.00"));

        mockRequest = new TransactionRequest();
        mockRequest.setAmount(new BigDecimal("50.00"));

        // 🟢 FIX 1: Use lenient() here.
        // This prevents "UnnecessaryStubbingException" in tests that don't use this repo.
        lenient().when(walletEntryRepository.findMaxTransactionId()).thenReturn(99L);
        
        // Manually Initialize the service so counter starts at 99
        walletService.init(); 
    }

    // --- CREATE WALLET TESTS ---

    @Test
    void createWallet_Success() {
        Long userId = 202L;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet created = walletService.createWallet(userId);

        assertNotNull(created);
        assertEquals(userId, created.getUserId());
        assertEquals(BigDecimal.ZERO, created.getBalance());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_AlreadyExists_ThrowsException() {
        Long userId = 101L;
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.createWallet(userId);
        });

        assertEquals("Wallet already exists for user 101", exception.getMessage());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    // --- DEBIT TESTS ---

    @Test
    void debit_Success() {
        // Arrange
        when(walletRepository.findByUserId(101L)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Wallet result = walletService.debit(101L, mockRequest);

        // Assert Balance
        assertEquals(new BigDecimal("50.00"), result.getBalance());

        // 🟢 FIX 2: Use ArgumentCaptor instead of argThat
        // This captures the object passed to save() so we can inspect it closely
        verify(walletEntryRepository).save(entryCaptor.capture());
        
        WalletEntry savedEntry = entryCaptor.getValue();
        
        assertNotNull(savedEntry);
        assertEquals("TXN100", savedEntry.getTransactionId()); // 99 + 1 = 100
        assertEquals(new BigDecimal("50.00"), savedEntry.getAmount());
        assertEquals(WalletEntry.TransactionType.DEBIT, savedEntry.getType());
    }

    @Test
    void debit_InsufficientFunds_ThrowsException() {
        mockWallet.setBalance(new BigDecimal("10.00"));
        when(walletRepository.findByUserId(101L)).thenReturn(Optional.of(mockWallet));

        assertThrows(InsufficientBalanceException.class, () -> {
            walletService.debit(101L, mockRequest);
        });

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    // --- CREDIT TESTS ---

    @Test
    void credit_Success() {
        when(walletRepository.findByUserId(101L)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet result = walletService.credit(101L, mockRequest);

        assertEquals(new BigDecimal("150.00"), result.getBalance());

        // 🟢 FIX 3: Use ArgumentCaptor again
        verify(walletEntryRepository).save(entryCaptor.capture());
        
        WalletEntry savedEntry = entryCaptor.getValue();
        
        assertEquals("TXN100", savedEntry.getTransactionId());
        assertEquals(WalletEntry.TransactionType.CREDIT, savedEntry.getType());
    }
}