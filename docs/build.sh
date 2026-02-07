#!/bin/bash
# Build all AsciiDoc files to HTML

set -e

echo "Building XML2JDTO Documentation..."
echo "=================================="
echo ""

# Check if asciidoctor is installed
if ! command -v asciidoctor &> /dev/null; then
    echo "Error: asciidoctor is not installed"
    echo ""
    echo "Install with:"
    echo "  Ubuntu/Debian: sudo apt-get install asciidoctor"
    echo "  macOS: brew install asciidoctor"
    echo "  Ruby gem: gem install asciidoctor"
    exit 1
fi

# Navigate to docs directory
cd "$(dirname "$0")"

# Build each .adoc file
echo "Building HTML files..."
for file in *.adoc; do
    if [ "$file" != "README.adoc" ]; then
        echo "  Converting $file..."
        asciidoctor -a stylesheet=custom.css "$file"
    fi
done

echo ""
echo "✓ Build complete!"
echo ""
echo "Generated files:"
ls -lh *.html
echo ""
echo "To view documentation, open: file://$(pwd)/index.html"
echo "Or run: python3 -m http.server 8000"
