# Build all AsciiDoc files to HTML
# PowerShell script for Windows

Write-Host "Building XML2JDTO Documentation..." -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check if asciidoctor is installed
$asciidoctor = Get-Command asciidoctor -ErrorAction SilentlyContinue
if (-not $asciidoctor) {
    Write-Host "Error: asciidoctor is not installed" -ForegroundColor Red
    Write-Host ""
    Write-Host "Install with Ruby gem: gem install asciidoctor"
    Write-Host "Or install Ruby from: https://rubyinstaller.org/"
    exit 1
}

# Navigate to script directory
Set-Location $PSScriptRoot

# Build each .adoc file
Write-Host "Building HTML files..." -ForegroundColor Green
Get-ChildItem -Filter *.adoc | Where-Object { $_.Name -ne "README.adoc" } | ForEach-Object {
    Write-Host "  Converting $($_.Name)..." -ForegroundColor Gray
    asciidoctor -a stylesheet=custom.css $_.Name
}

Write-Host ""
Write-Host "✓ Build complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Generated files:"
Get-ChildItem -Filter *.html | Format-Table Name, Length, LastWriteTime
Write-Host ""
Write-Host "To view documentation, open: file://$(Get-Location)/index.html"
Write-Host "Or run: python -m http.server 8000"
