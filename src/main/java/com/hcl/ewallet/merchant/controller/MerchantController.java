package com.hcl.ewallet.merchant.controller;

import com.hcl.ewallet.merchant.dto.CreateMerchantRequest;
import com.hcl.ewallet.merchant.dto.CreditRequest;
import com.hcl.ewallet.merchant.dto.WalletResponse;
import com.hcl.ewallet.merchant.entity.MerchantTransaction;
import com.hcl.ewallet.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/{merchantId}/credit")
    public String creditMerchant(
            @PathVariable String merchantId,
            @RequestBody CreditRequest request) {

        merchantService.creditMerchant(
                merchantId,
                request.getAmount(),
                request.getCustomerId()
        );

        return "Merchant credited successfully";
    }

    @PostMapping
    public String createMerchant(@RequestBody CreateMerchantRequest request) {

        String merchantId = merchantService.createMerchant(
                request.getName(),
                request.getCurrency()
        );

        return merchantId;
    }

    @GetMapping("/{merchantId}/wallet")
    public WalletResponse getWallet(@PathVariable String merchantId) {

        return merchantService.getWalletByMerchantId(merchantId);
    }

    @GetMapping("/{merchantId}/transactions")
    public List<MerchantTransaction> getTransactions(@PathVariable String merchantId) {

        return merchantService.getTransactionsByMerchant(merchantId);
    }
}
