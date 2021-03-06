/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.ballerinalang.langserver.completions.providers.subproviders;

import org.antlr.v4.runtime.Token;
import org.ballerinalang.langserver.common.UtilSymbolKeys;
import org.ballerinalang.langserver.compiler.LSContext;
import org.ballerinalang.langserver.compiler.LSPackageLoader;
import org.ballerinalang.langserver.compiler.common.modal.BallerinaPackage;
import org.ballerinalang.langserver.completions.CompletionKeys;
import org.ballerinalang.langserver.completions.util.ItemResolverConstants;
import org.ballerinalang.langserver.completions.util.Priority;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Completion Item Resolver for the Package name context.
 */
public class PackageNameCompletionProvider extends AbstractSubCompletionProvider {
    @Override
    public List<CompletionItem> resolveItems(LSContext ctx) {
        ArrayList<CompletionItem> completionItems = new ArrayList<>();
        List<BallerinaPackage> packagesList = new ArrayList<>();
        Stream.of(LSPackageLoader.getSdkPackages(), LSPackageLoader.getHomeRepoPackages())
                .forEach(packagesList::addAll);
        List<String> poppedTokens = ctx.get(CompletionKeys.FORCE_CONSUMED_TOKENS_KEY)
                .stream()
                .map(Token::getText)
                .collect(Collectors.toList());
        
        if (poppedTokens.contains(UtilSymbolKeys.SLASH_KEYWORD_KEY)) {
            String orgName = poppedTokens.get(poppedTokens.indexOf(UtilSymbolKeys.SLASH_KEYWORD_KEY) - 1);
            completionItems.addAll(this.getPackageNameCompletions(orgName, packagesList));
        } else if (poppedTokens.contains(UtilSymbolKeys.IMPORT_KEYWORD_KEY)) {
            completionItems.addAll(this.getItemsIncludingOrgName(packagesList));
        }

        return completionItems;
    }

    private ArrayList<CompletionItem> getItemsIncludingOrgName(List<BallerinaPackage> packagesList) {
        List<String> orgNames = new ArrayList<>();
        ArrayList<CompletionItem> completionItems = new ArrayList<>();

        packagesList.forEach(pkg -> {
            String fullPkgNameLabel = pkg.getOrgName() + "/" + pkg.getPackageName();
            // Do not add the semicolon with the insert text since the user should be allowed to use the as keyword
            CompletionItem fullPkgImport = getImportCompletion(fullPkgNameLabel, fullPkgNameLabel);
            fullPkgImport.setSortText(Priority.PRIORITY120.toString());
            completionItems.add(fullPkgImport);
            if (!orgNames.contains(pkg.getOrgName())) {
                CompletionItem orgNameImport = getImportCompletion(pkg.getOrgName(), (pkg.getOrgName() + "/"));
                orgNameImport.setSortText(Priority.PRIORITY110.toString());
                completionItems.add(orgNameImport);
                orgNames.add(pkg.getOrgName());
            }
        });

        return completionItems;
    }

    private ArrayList<CompletionItem> getPackageNameCompletions(String orgName, List<BallerinaPackage> packagesList) {
        ArrayList<CompletionItem> completionItems = new ArrayList<>();
        List<String> pkgNameLabels = new ArrayList<>();

        packagesList.forEach(ballerinaPackage -> {
            String label = ballerinaPackage.getPackageName();
            if (orgName.equals(ballerinaPackage.getOrgName()) && !pkgNameLabels.contains(label)) {
                pkgNameLabels.add(label);
                // Do not add the semi colon at the end of the insert text since the user might type the as keyword
                completionItems.add(getImportCompletion(label, label));
            }
        });
        
        return completionItems;
    }
    
    private static CompletionItem getImportCompletion(String label, String insertText) {
        CompletionItem item = new CompletionItem();
        item.setLabel(label);
        item.setInsertText(insertText);
        item.setKind(CompletionItemKind.Module);
        item.setDetail(ItemResolverConstants.PACKAGE_TYPE);
        
        return item;
    }
}
